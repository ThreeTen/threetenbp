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

import javax.time.Duration;
import javax.time.period.PeriodFields;

/**
 * A unit of time for measuring a period, such as 'Days' or 'Minutes'.
 * <p>
 * <code>PeriodUnit</code> is an immutable definition of a unit of human-scale time.
 * For example, humans typically measure periods of time in units of years, months,
 * days, hours, minutes and seconds. These concepts are defined by instances of
 * this class defined in the chronology classes.
 * <p>
 * Units are either basic or derived. A derived unit can be converted accurately to
 * another smaller unit. A basic unit is fundamental, and has no smaller representation.
 * For example years are a derived unit consisting of 12 months, where a month is
 * a basic unit.
 * <p>
 * PeriodUnit is immutable and thread-safe.
 *
 * @author Stephen Colebourne
 */
public final class PeriodUnit
        implements Comparable<PeriodUnit>, Serializable {
    // TODO: serialization

    /** The serialization version. */
    private static final long serialVersionUID = 1L;

    /**
     * The chronology of the unit, not null.
     */
    private final Chronology chronology;
    /**
     * The id of the unit, not null.
     */
    private final String id;
    /**
     * The name of the unit, not null.
     */
    private final String name;
    /**
     * The period equivalent to this unit.
     */
    private final PeriodFields equivalentPeriod;
    /**
     * The estimated duration of the unit, not null.
     */
    private final Duration estimatedDuration;

    /**
     * Constructor used to create a base unit that cannot be derived.
     * <p>
     * A base unit cannot be derived from any smaller unit.
     * For example, an ISO month period cannot be derived from any other smaller period.
     *
     * @param chronology  the chronology, not null
     * @param name  the name of the type, not null
     * @param estimatedDuration  the estimated duration of one unit of this period, not null
     */
    public PeriodUnit(
            Chronology chronology,
            String name,
            Duration estimatedDuration) {
        // avoid possible circular references by using inline NPE checks
        if (chronology == null) {
            throw new NullPointerException("Chronology must not be null");
        }
        if (name == null) {
            throw new NullPointerException("Name must not be null");
        }
        if (estimatedDuration == null) {
            throw new NullPointerException("Estimated duration must not be null");
        }
        if (estimatedDuration.isNegative() || estimatedDuration.isZero()) {
            throw new IllegalArgumentException("Alternate period must be positive and non-zero");
        }
        this.chronology = chronology;
        this.id = chronology.getName() + '.' + name;
        this.name = name;
        this.equivalentPeriod = null;
        this.estimatedDuration = estimatedDuration;
    }

    /**
     * Constructor used to create a derived unit.
     * <p>
     * A derived unit is created as a multiple of a smaller unit.
     * For example, an ISO year period can be derived as 12 ISO month periods.
     * <p>
     * The estimated duration is calculated using {@link PeriodFields#toEstimatedDuration()}.
     *
     * @param chronology  the chronology, not null
     * @param name  the name of the type, not null
     * @param equivalentPeriod  the period this is derived from, not null
     * @throws ArithmeticException if the estimated duration is too large
     */
    public PeriodUnit(
            Chronology chronology,
            String name,
            PeriodFields equivalentPeriod) {
        // avoid possible circular references by using inline NPE checks
        if (chronology == null) {
            throw new NullPointerException("Chronology must not be null");
        }
        if (name == null) {
            throw new NullPointerException("Name must not be null");
        }
        if (equivalentPeriod == null) {
            throw new NullPointerException("Equivalent period must not be null");
        }
        if (equivalentPeriod.isPositive() == false || equivalentPeriod.isZero()) {
            throw new IllegalArgumentException("Equivalent period must be positive and non-zero");
        }
        this.chronology = chronology;
        this.id = chronology.getName() + '.' + name;
        this.name = name;
        this.equivalentPeriod = equivalentPeriod;
        this.estimatedDuration = equivalentPeriod.toEstimatedDuration();
    }

    /**
     * Package private constructor used for to enhance system startup performance.
     *
     * @param chronology  the chronology, not null
     * @param name  the name of the type, not null
     * @param equivalentPeriod  the period this is derived from, null if no equivalent
     * @param estimatedDuration  the estimated duration of one unit of this period, not null
     */
    PeriodUnit(
            Chronology chronology,
            String name,
            PeriodFields equivalentPeriod,
            Duration estimatedDuration) {
        // input known to be valid, don't call this by reflection!
        this.chronology = chronology;
        this.id = chronology.getName() + '.' + name;
        this.name = name;
        this.equivalentPeriod = equivalentPeriod;
        this.estimatedDuration = estimatedDuration;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the chronology of the unit.
     *
     * @return the chronology of the unit, never null
     */
    public Chronology getChronology() {
        return chronology;
    }

    /**
     * Gets the ID of the unit.
     * <p>
     * The ID is of the form 'ChronologyName.RuleName'.
     * No two fields should have the same id.
     *
     * @return the id of the unit, never null
     */
    public String getID() {
        return id;
    }

    /**
     * Gets the name of the unit.
     * <p>
     * Implementations should use the name that best represents themselves.
     * Most units will have a plural name, such as 'Years' or 'Minutes'.
     *
     * @return the name of the unit, never null
     */
    public String getName() {
        return name;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the period that is equivalent to this unit.
     * <p>
     * Most period units are related to other units.
     * For example, an hour might be represented as 60 minutes.
     * Thus, if this is the hour unit, then this method would return 60 minutes.
     *
     * @return the alternate period, null if none
     */
    public PeriodFields getEquivalentPeriod() {
        return equivalentPeriod;
    }

    /**
     * Gets an estimate of the duration of the unit in seconds.
     * This is used for comparing units.
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
     * The comparison is based on the {@link #getEstimatedDuration() estimated duration}.
     *
     * @param other  the other type to compare to, not null
     * @return the comparator result, negative if less, positive if greater, zero if equal
     * @throws NullPointerException if other is null
     */
    public int compareTo(PeriodUnit other) {
        return getEstimatedDuration().compareTo(other.getEstimatedDuration());
    }

    //-----------------------------------------------------------------------
    /**
     * Compares two units based on their ID.
     *
     * @return true if the units are the same
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        return id.equals(((PeriodUnit) obj).id);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a hash code based on the ID.
     *
     * @return a description of the unit
     */
    @Override
    public int hashCode() {
        return id.hashCode();
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a string representation of the unit.
     *
     * @return a description of the unit, never null
     */
    @Override
    public String toString() {
        return id;
    }

}
