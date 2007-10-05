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
package javax.time.duration;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * The unit defining how a measurable duration of time operates.
 * <p>
 * Duration unit implementations define how a field like 'days' operates.
 * This includes the duration name and relationship to other durations like hour.
 * <p>
 * DurationUnit is an abstract class and must be implemented with care to
 * ensure other classes in the framework operate correctly.
 * All instantiable subclasses must be final, immutable and thread-safe.
 *
 * @author Stephen Colebourne
 */
public class DurationUnit implements Comparable<DurationUnit> {

    /**
     * Map of all instances of <code>DurationUnit</code>.
     */
    private static final ConcurrentMap<String, DurationUnit> INSTANCES =
        new ConcurrentHashMap<String, DurationUnit>();

    /** The name of the rule, not null. */
    private final String name;
    /** The alternate duration, expressing this field in terms of another. */
    private final Durational alternateDuration;

    //-----------------------------------------------------------------------
    /**
     * Creates a duration unit.
     *
     * @param name  the name of the unit, not null
     * @return the created duration unit, never null
     * @throws IllegalArgumentException if there is already a unit with the specified name
     */
    public static DurationUnit createUnit(String name) {
        if (name == null) {
            throw new NullPointerException("Duration unit name must not be null");
        }
        DurationUnit unit = new DurationUnit(name, null);
        unit = INSTANCES.putIfAbsent(name, unit);
        if (unit != null) {
            throw new IllegalArgumentException("Duration unit '" + name + "' already exists");
        }
        return unit;
    }

    /**
     * Gets a unit from a name.
     *
     * @param name  the name of the unit, not null
     * @return the previously created duration unit, never null
     * @throws IllegalArgumentException if there is no unit with the specified name
     */
    public static DurationUnit unitForName(String name) {
        if (name == null) {
            throw new NullPointerException("Duration unit name must not be null");
        }
        DurationUnit unit = INSTANCES.get(name);
        if (unit == null) {
            throw new IllegalArgumentException("Duration unit '" + name + "' not found");
        }
        return unit;
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor.
     *
     * @param name  the name of the rule, not null
     * @param alternateDuration  alternate duration that this field can be expressed in, null if none
     */
    protected DurationUnit(String name, Durational alternateDuration) {
        super();
        // TODO: Check not null
        this.name = name;
        this.alternateDuration = alternateDuration;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the name of the time duration type.
     *
     * @return the name of the time field type, never null
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the alternate duration that this field can be expressed as.
     * For example, a day can be represented as 24 hours.
     *
     * @return the alternate duration, null if none
     */
    public Durational getAlternateDuration() {
        return alternateDuration;
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this DurationFieldRule to another based on the average duration
     * of the field.
     *
     * @param other  the other type to compare to, not null
     * @return the comparator result, negative if less, postive if greater, zero if equal
     * @throws NullPointerException if other is null
     */
    public int compareTo(DurationUnit other) {
        return 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a string representation of the time amount type.
     *
     * @return a description of the amount of time
     */
    @Override
    public String toString() {
        return getName();
    }

}
