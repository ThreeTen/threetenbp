/*
 * Copyright (c) 2010 Stephen Colebourne & Michael Nascimento Santos
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.time.Duration;

/**
 * A unit of time for measuring a period, such as 'Days' or 'Minutes'.
 * <p>
 * {@code PeriodUnit} is an immutable definition of a unit of human-scale time.
 * For example, humans typically measure periods of time in units of years, months,
 * days, hours, minutes and seconds. These concepts are defined by instances of
 * this class defined in the chronology classes.
 * <p>
 * Units are either basic or derived. A derived unit can be converted accurately to
 * another smaller unit. A basic unit is fundamental, and has no smaller representation.
 * For example years are a derived unit consisting of 12 months, where a month is
 * a basic unit.
 * <p>
 * PeriodUnit is an abstract class and must be implemented with care
 * to ensure other classes in the framework operate correctly.
 * All instantiable implementations must be final, immutable and thread-safe.
 * <p>
 * The subclass is fully responsible for serialization as all fields in this class are
 * transient. The subclass must use {@code readResolve} to replace the deserialized
 * class with a valid one created via a constructor.
 *
 * @author Stephen Colebourne
 */
public abstract class PeriodUnit
        implements Comparable<PeriodUnit>, Serializable {

    /**
     * The serialization version.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The name of the unit, not null.
     */
    private transient final String name;
    /**
     * The estimated duration of the unit, not null.
     */
    private transient final Duration estimatedDuration;
    /**
     * The cache of periods equivalent to this unit, not null.
     */
    private transient final List<PeriodField> equivalentPeriods;
    /**
     * The cache of the unit hash code.
     */
    private transient final int hashCode;

    /**
     * Constructor to create a base unit that cannot be derived.
     * <p>
     * A base unit cannot be derived from any smaller unit.
     * For example, an ISO month period cannot be derived from any other smaller period.
     * <p>
     * This method is typically only used when writing a {@link Chronology}.
     *
     * @param name  the name of the type, not null
     * @param estimatedDuration  the estimated duration of one unit of this period, not null
     * @throws IllegalArgumentException if the duration is zero or negative
     */
    protected PeriodUnit(String name, Duration estimatedDuration) {
        ISOChronology.checkNotNull(name, "Name must not be null");
        ISOChronology.checkNotNull(estimatedDuration, "Estimated duration must not be null");
        if (estimatedDuration.isNegative() || estimatedDuration.isZero()) {
            throw new IllegalArgumentException("Alternate period must not be negative or zero");
        }
        this.name = name;
        this.estimatedDuration = estimatedDuration;
        this.equivalentPeriods = buildEquivalentPeriods(null);
        this.hashCode = name.hashCode() ^ estimatedDuration.hashCode() ^ 0;
    }

    /**
     * Constructor to create a unit that is derived from another smaller unit.
     * <p>
     * A derived unit is created as a multiple of a smaller unit.
     * For example, an ISO year period can be derived as 12 ISO month periods.
     * <p>
     * The estimated duration is calculated using {@link PeriodField#toEstimatedDuration()}.
     * <p>
     * This method is typically only used when writing a {@link Chronology}.
     *
     * @param name  the name of the type, not null
     * @param equivalentPeriod  the period this is derived from, not null
     * @throws IllegalArgumentException if the period is zero or negative
     * @throws ArithmeticException if the equivalent period calculation overflows
     */
    protected PeriodUnit(String name, PeriodField equivalentPeriod) {
        ISOChronology.checkNotNull(name, "Name must not be null");
        ISOChronology.checkNotNull(equivalentPeriod, "Equivalent period must not be null");
        if (equivalentPeriod.getAmount() <= 0) {
            throw new IllegalArgumentException("Equivalent period must not be negative or zero");
        }
        this.name = name;
        this.estimatedDuration = equivalentPeriod.toEstimatedDuration();
        this.equivalentPeriods = buildEquivalentPeriods(equivalentPeriod);
        this.hashCode = name.hashCode() ^ estimatedDuration.hashCode() ^ equivalentPeriod.hashCode();
    }

    /**
     * Constructor used by ISOChronology.
     *
     * @param name  the name of the type, not null
     * @param equivalentPeriod  the period this is derived from, null if no equivalent
     * @param estimatedDuration  the estimated duration of one unit of this period, not null
     * @throws ArithmeticException if the equivalent period calculation overflows
     */
    PeriodUnit(String name, PeriodField equivalentPeriod, Duration estimatedDuration) {
        // input known to be valid
        this.name = name;
        this.estimatedDuration = estimatedDuration;
        this.equivalentPeriods = buildEquivalentPeriods(equivalentPeriod);
        this.hashCode = name.hashCode() ^ estimatedDuration.hashCode() ^
                (equivalentPeriod != null ? equivalentPeriod.hashCode() : 0);
    }

    /**
     * Helper method for constructors to built the equivalent periods.
     * 
     * @param equivalentPeriod  the period this is derived from, null if no equivalent
     * @return the list of equivalent periods, never null
     */
    private static List<PeriodField> buildEquivalentPeriods(PeriodField equivalentPeriod) {
        if (equivalentPeriod == null) {
            return Collections.emptyList();
        }
        List<PeriodField> equivalents = new ArrayList<PeriodField>();
        equivalents.add(equivalentPeriod);
        long multiplier = equivalentPeriod.getAmount();
        List<PeriodField> baseEquivalents = equivalentPeriod.getUnit().getEquivalentPeriods();
        for (int i = 0; i < baseEquivalents.size(); i++) {
            equivalents.add(baseEquivalents.get(i).multipliedBy(multiplier));
        }
        return Collections.unmodifiableList(equivalents);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the name of the unit, used as an identifier for the unit.
     * <p>
     * Implementations should use the name that best represents themselves.
     * Most units will have a plural name, such as 'Years' or 'Minutes'.
     * The name is not localized.
     *
     * @return the name of the unit, never null
     */
    public String getName() {
        return name;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the periods that are equivalent to this unit.
     * <p>
     * Most units are related to other units.
     * For example, an hour might be represented as 60 minutes or 3600 seconds.
     * Thus, if this is the 'Hour' unit, then this method would return a list
     * including both '60 Minutes', '3600 Seconds' and any other equivalent periods.
     * <p>
     * Registered conversion is stored from larger units to smaller units.
     * Thus, {@code monthsUnit.getEquivalentPeriods()} will not contain the unit for years.
     * Note that the returned list does <i>not</i> contain this unit.
     * <p>
     * The list will be unmodifiable and sorted, from largest unit to smallest.
     *
     * @return the equivalent periods, may be empty, never null
     */
    public List<PeriodField> getEquivalentPeriods() {
        return equivalentPeriods;
    }

    /**
     * Gets the period in the specified unit that is equivalent to this unit.
     * <p>
     * Most units are related to other units.
     * For example, an hour might be represented as 60 minutes or 3600 seconds.
     * Thus, if this is the 'Hour' unit and the 'Seconds' unit is requested,
     * then this method would return '3600 Seconds'.
     * <p>
     * Registered conversion is stored from larger units to smaller units.
     * Thus, {@code monthsUnit.getEquivalentPeriod(yearsUnit)} will return null.
     * Note that if the unit specified is this unit, then a period of 1 of this unit is returned.
     *
     * @param requiredUnit  the required unit, not null
     * @return the equivalent period, null if no equivalent in that unit
     */
    public PeriodField getEquivalentPeriod(PeriodUnit requiredUnit) {
        ISOChronology.checkNotNull(requiredUnit, "PeriodUnit must not be null");
        for (PeriodField equivalent : equivalentPeriods) {
            if (equivalent.getUnit().equals(requiredUnit)) {
                return equivalent;
            }
        }
        if (requiredUnit.equals(this)) {
            return PeriodField.of(1, this);  // cannot be cached in constructor, as would be unsafe publication
        }
        return null;
    }

    /**
     * Checks whether this unit can be converted to the specified unit.
     * <p>
     * Most units are related to other units.
     * For example, an hour might be represented as 60 minutes or 3600 seconds.
     * This method checks if this unit has a registered conversion to the specified unit.
     * <p>
     * Registered conversion is stored from larger units to smaller units.
     * Thus, {@code monthsUnit.isConvertibleTo(yearsUnit)} will return false.
     * Note that this unit is convertible to itself.
     *
     * @param unit  the unit, null returns false
     * @return true if this unit is convertible or equal to the specified unit
     */
    public boolean isConvertibleTo(PeriodUnit unit) {
        for (PeriodField equivalent : equivalentPeriods) {
            if (equivalent.getUnit().equals(unit)) {
                return true;
            }
        }
        return this.equals(unit);
    }

    /**
     * Gets the base unit of this unit.
     * <p>
     * Most units are related to other units.
     * For example, an hour might be represented as 60 minutes or 3600 seconds.
     * The base unit is the smallest unit that this unit defines an equivalence to.
     * <p>
     * For example, most time units are ultimately convertible to nanoseconds,
     * thus nanoseconds is the base unit.
     *
     * @return the base unit, never null
     */
    public PeriodUnit getBaseUnit() {
        if (equivalentPeriods.isEmpty()) {
            return this;
        }
        return equivalentPeriods.get(equivalentPeriods.size() - 1).getUnit();
    }

    //-----------------------------------------------------------------------
    /**
     * Gets an estimate of the duration of the unit in seconds.
     * <p>
     * Each unit has a duration which is a reasonable estimate.
     * For those units which can be derived ultimately from nanoseconds, the
     * estimated duration will be accurate. For other units, it will be an estimate.
     * <p>
     * One key use for the estimated duration is to implement {@link Comparable}.
     *
     * @return the estimate of the duration in seconds, never null
     */
    public Duration getEstimatedDuration() {
        return estimatedDuration;
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this unit to another.
     * <p>
     * The comparison is based primarily on the {@link #getEstimatedDuration() estimated duration}.
     * If that is equal, the name is compared using standard string comparison.
     * Finally, the first equivalent period is checked, with basic units before derived ones.
     *
     * @param other  the other type to compare to, not null
     * @return the comparator result, negative if less, positive if greater, zero if equal
     * @throws NullPointerException if other is null
     */
    public int compareTo(PeriodUnit other) {
        int cmp = estimatedDuration.compareTo(other.estimatedDuration);
        if (cmp == 0) {
            cmp = name.compareTo(other.name);
            if (cmp == 0) {
                cmp = (equivalentPeriods.size() - other.equivalentPeriods.size());
                if (cmp == 0 && equivalentPeriods.size() > 0) {
                    cmp = (equivalentPeriods.get(0).compareTo(other.equivalentPeriods.get(0)));
                }
            }
        }
        return cmp;
    }

    //-----------------------------------------------------------------------
    /**
     * Compares two units based on the name, estimated duration and equivalent period.
     *
     * @return true if the units are the same
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof PeriodUnit) {
            PeriodUnit other = (PeriodUnit) obj;
            return name.equals(other.name) &&
                    estimatedDuration.equals(other.estimatedDuration) &&
                    equivalentPeriods.size() == other.equivalentPeriods.size() &&
                    (equivalentPeriods.size() == 0 || equivalentPeriods.get(0).equals(other.equivalentPeriods.get(0)));
        }
        return false;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a hash code based on the name, estimated duration and equivalent period.
     *
     * @return a suitable hash code
     */
    @Override
    public int hashCode() {
        return hashCode;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a string representation of the unit.
     * <p>
     * The string representation is the same as the name.
     *
     * @return the unit name, never null
     */
    @Override
    public String toString() {
        return name;
    }

}
