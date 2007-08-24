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
package javax.time.part;

import java.io.Serializable;
import java.util.Map;

import javax.math.Fraction;
import javax.math.MathUtils;

/**
 * A basic part of the human calendaring system.
 * <p>
 * Each TimePart subclass represents a conceptual part of a calendar,
 * such as year, day or minute. The part is the duration of that element
 * of the calendar, not the field. Thus the part representing a day
 * applies equally to DayOfWeek, DayOfMonth and DayOfYear.
 *
 * @author Stephen Colebourne
 */
public abstract class TimePart
        implements Comparable<TimePart>, Serializable {

    /**
     * Gets an approximate duration of the part used to determine
     * comparative size.
     * <p>
     * Negative numbers should be used if the duration is less than a second
     * to represent one divided by the value. Thus, a millisecond would be
     * represented by -1000, as it is 1/1000th of a second.
     * <p>
     * Infinitely large durations should be represented by with respect to
     * <code>Long.MAX_VALUE</code>. The whole time-line is represented by
     * <code>Long.MAX_VALUE</code> and only <code>Forever</code> should return
     * this value.
     * <p>
     * Other long durations are generally relative to the time-line, such as
     * the standard eras BC/AD (BCE/CE) which are hald the time-line, would be
     * represented by <code>Long.MAX_VALUE / 2</code>.
     *
     * @return the approximate duration of the time part
     */
    protected abstract long getComparisonDurationSeconds();

    /**
     * Gets the name of the time part.
     * <p>
     * Subclasses should return the singular name of the time part, such
     * as 'Month' or 'Minute'.
     *
     * @return the name of the time part, never null
     */
    public abstract String getName();

    //-----------------------------------------------------------------------
    /**
     * Gets the duration ratio between this part and another.
     * <p>
     * The ratio is what you need to multiply by to convert an amount in
     * units of one part, to an amount in units of another part.
     *
     * @param otherPart  the other part to get the duration ratio to, not null
     * @return the converted amount of time
     */
    public abstract Fraction getDurationRatio(TimePart otherPart);

    /**
     * Finds the duration ratio.
     *
     * @param otherPart  the other time part to find the ratio to, not null
     * @param map  the conversion map, not null
     * @return the conversion ratio
     */
    protected Fraction findDurationRatio(TimePart otherPart, Map<TimePart, Fraction> map) {
        Fraction fraction = map.get(otherPart);
        if (fraction != null) {
            return fraction;
        }
        fraction = otherPart.getDurationRatio(this);
        if (fraction != null) {
            return fraction.inverted();
        }
        // TODO: Derive indirectly
        throw new IllegalArgumentException("Unknown duration ratio: " + this + " to " + otherPart);
    }

//    /**
//     * Gets the amount of time, in another time part, that is equal to this part.
//     * <p>
//     * The ratio is what you need to multiply by to convert an amount in
//     * units of one part, to an amount in units of another part.
//     *
//     * @param otherPart  the other part to get the duration ratio to, not null
//     * @return the amount of time that this part is normally expressed as
//     */
//    public abstract TimeAmount<?> getPrimaryTimeAmount();

    //-----------------------------------------------------------------------
    /**
     * Compares this time part to another based on approximate duration.
     * This will return the types from shortest to longest.
     *
     * @param other  the time amount to compare to, not null
     * @return the comparator result, negative if less, postive if greater, zero if equal
     * @throws NullPointerException if other is null
     */
    public int compareTo(TimePart other) {
        return MathUtils.safeCompare(getComparisonDurationSeconds(), other.getComparisonDurationSeconds());
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
