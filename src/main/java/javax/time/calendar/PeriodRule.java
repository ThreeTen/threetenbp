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

/**
 * The rule defining how a single calendrical period operates.
 * <p>
 * Period rules represent the basic elements which are used to build calendar systems.
 * Examples include days, years and hours.
 * <p>
 * PeriodRule is immutable and thread-safe.
 *
 * @author Stephen Colebourne
 */
public final class PeriodRule
        implements Comparable<PeriodRule>, Serializable {
    // TODO: serialization

    /** The serialization version. */
    private static final long serialVersionUID = 1L;

    /** The chronology of the rule, not null. */
    private final Chronology chronology;
    /** The id of the rule, not null. */
    private final String id;
    /** The name of the rule, not null. */
    private final String name;
    /** The estimated duration of the rule, not null. */
    private final Duration estimatedDuration;

    /**
     * Constructor used to create a rule.
     *
     * @param chronology  the chronology, not null
     * @param name  the name of the type, not null
     * @param estimatedDuration  the estimated duration, not null
     */
    public PeriodRule(
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
        this.chronology = chronology;
        this.id = chronology.getName() + '.' + name;
        this.name = name;
        this.estimatedDuration = estimatedDuration;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the chronology of the rule.
     *
     * @return the chronology of the rule, never null
     */
    public Chronology getChronology() {
        return chronology;
    }

    /**
     * Gets the ID of the rule.
     * <p>
     * The ID is of the form 'ChronologyName.RuleName'.
     * No two fields should have the same id.
     *
     * @return the id of the rule, never null
     */
    public String getID() {
        return id;
    }

    /**
     * Gets the name of the rule.
     * <p>
     * Implementations should use the name that best represents themselves.
     * Most rules will have a plural name, such as 'Years' or 'Minutes'.
     *
     * @return the name of the rule, never null
     */
    public String getName() {
        return name;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets an estimate of the duration of the unit in seconds.
     * This is used for comparing period rules.
     *
     * @return the estimate of the duration in seconds, never null
     */
    public Duration getEstimatedDuration() {
        return estimatedDuration;
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this <code>PeriodRule</code> to another.
     * <p>
     * The comparison is based on the estimated duration.
     *
     * @param other  the other type to compare to, not null
     * @return the comparator result, negative if less, positive if greater, zero if equal
     * @throws NullPointerException if other is null
     */
    public int compareTo(PeriodRule other) {
        return getEstimatedDuration().compareTo(other.getEstimatedDuration());
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
        return id.equals(((PeriodRule) obj).id);
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
