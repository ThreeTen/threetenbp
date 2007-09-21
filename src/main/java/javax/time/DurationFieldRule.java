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
package javax.time;

import javax.time.duration.DurationField;

/**
 * The rule defining how a measurable duration of time operates.
 * <p>
 * Duration field rule implementations define how a field like 'days' operates.
 * This includes the duration name and relationship to other durations like hour.
 * <p>
 * DurationFieldRule is an abstract class and must be implemented with care to
 * ensure other classes in the framework operate correctly.
 * All instantiable subclasses must be final, immutable and thread-safe.
 *
 * @author Stephen Colebourne
 */
public abstract class DurationFieldRule implements Comparable<DurationFieldRule> {

    /** The name of the rule, not null. */
    private final String name;
    /** The relative field, expressing this field in terms of another. */
    private final DurationField relativeField;

    /**
     * Constructor.
     *
     * @param name  the name of the rule, not null
     * @param relativeField  alternate field that this field can be expressed in, null if none
     */
    protected DurationFieldRule(String name, DurationField relativeField) {
        super();
        // TODO: Check not null
        this.name = name;
        this.relativeField = relativeField;
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
     * Gets the alternate field that this field can be expressed as.
     * For example, a day can be represented as 24 hours.
     *
     * @return the relative field, null if none
     */
    public DurationField getRelativeField() {
        return relativeField;
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
    public int compareTo(DurationFieldRule other) {
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
