/*
 * Copyright (c) 2009-2010, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time.calendar.zone;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;

import javax.time.Instant;
import javax.time.calendar.LocalDateTime;
import javax.time.calendar.OffsetDateTime;
import javax.time.calendar.Period;
import javax.time.calendar.ZoneOffset;

/**
 * A transition between two offsets caused by a discontinuity in the local time-line.
 * <p>
 * A transition between two offsets is normally the result of a daylight savings cutover.
 * The discontinuity is normally a gap in spring and an overlap in autumn.
 * {@code ZoneOffsetTransition} models the transition between the two offsets.
 * <p>
 * There are two types of transition - a gap and an overlap.
 * Gaps occur where there are local date-times that simply do not not exist.
 * An example would be when the offset changes from {@code +01:00} to {@code +02:00}.
 * Overlaps occur where there are local date-times that exist twice.
 * An example would be when the offset changes from {@code +02:00} to {@code +01:00}.
 * <p>
 * ZoneOffsetTransition is immutable and thread-safe.
 *
 * @author Stephen Colebourne
 */
public final class ZoneOffsetTransition implements Comparable<ZoneOffsetTransition>, Serializable {

    /**
     * A serialization identifier for this class.
     */
    private static final long serialVersionUID = 1L;
    /**
     * The transition date-time with the offset before the transition.
     */
    private final OffsetDateTime transition;
    /**
     * The transition date-time with the offset after the transition.
     */
    private final OffsetDateTime transitionAfter;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance defining a transition between two offsets.
     * <p>
     * Applications should normally obtain an instance from {@link ZoneRules}.
     * This constructor is intended for use by implementors of {@code ZoneRules}.
     *
     * @param transition  the transition date-time with the offset before the transition, not null
     * @param offsetAfter  the offset at and after the transition, not null
     */
    public static ZoneOffsetTransition of(OffsetDateTime transition, ZoneOffset offsetAfter) {
        ZoneRules.checkNotNull(transition, "OffsetDateTime must not be null");
        ZoneRules.checkNotNull(transition, "ZoneOffset must not be null");
        if (transition.getOffset().equals(offsetAfter)) {
            throw new IllegalArgumentException("Offsets must not be equal");
        }
        return new ZoneOffsetTransition(transition, offsetAfter);
    }

    /**
     * Creates an instance defining a transition between two offsets.
     *
     * @param transition  the transition date-time with the offset before the transition, not null
     * @param offsetAfter  the offset at and after the transition, not null
     */
    ZoneOffsetTransition(OffsetDateTime transition, ZoneOffset offsetAfter) {
        this.transition = transition;
        this.transitionAfter = transition.withOffsetSameInstant(offsetAfter);  // cached for performance
    }

    //-------------------------------------------------------------------------
    /**
     * Uses a serialization delegate.
     *
     * @return the replacing object, never null
     */
    private Object writeReplace() {
        return new Ser(Ser.ZOT, this);
    }

    /**
     * Writes the state to the stream.
     * @param out  the output stream, not null
     * @throws IOException if an error occurs
     */
    void writeExternal(DataOutput out) throws IOException {
        Ser.writeEpochSecs(transition.toEpochSeconds(), out);
        Ser.writeOffset(transition.getOffset(), out);
        Ser.writeOffset(transitionAfter.getOffset(), out);
    }

    /**
     * Reads the state from the stream.
     * @param in  the input stream, not null
     * @return the created object, never null
     * @throws IOException if an error occurs
     */
    static ZoneOffsetTransition readExternal(DataInput in) throws IOException {
        long epochSeconds = Ser.readEpochSecs(in);
        ZoneOffset before = Ser.readOffset(in);
        ZoneOffset after = Ser.readOffset(in);
        return ZoneOffsetTransition.of(OffsetDateTime.ofEpochSeconds(epochSeconds, before), after);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the transition instant.
     * <p>
     * This is the instant of the discontinuity, which is defined as the first
     * instant that the 'after' offset applies. This instant can be also obtained
     * using the {@link #getDateTime() 'before' offset} or the
     * {@link #getDateTimeAfter() 'after' offset}.
     *
     * @return the transition instant, not null
     */
    public Instant getInstant() {
        return transition.toInstant();
    }

    /**
     * Gets the local date-time at the transition which is expressed relative to
     * the 'before' offset.
     * <p>
     * This is the date-time where the discontinuity begins, and as such it never
     * actually occurs. This method is simply {@code getDateTime().toLocalDateTime()}
     * <p>
     * This value expresses the date-time normally used in verbal communications.
     * For example 'the clocks will move forward one hour tonight at 1am'.
     *
     * @return the transition date-time expressed with the before offset, not null
     */
    public LocalDateTime getLocal() {
        return transition.toLocalDateTime();
    }

    /**
     * Gets the transition instant date-time expressed with the 'before' offset.
     * <p>
     * This is the date-time where the discontinuity begins, and as such it never
     * actually occurs (as the 'after' offset is actually used at this instant).
     * This is the same instant as {@link #getDateTimeAfter()} but with the 'before' offset.
     *
     * @return the transition date-time expressed with the before offset, not null
     */
    public OffsetDateTime getDateTime() {
        return transition;
    }

    /**
     * Gets the transition date-time expressed with the 'after' offset.
     * <p>
     * This is the first date-time after the discontinuity, when the new offset applies.
     * This is the same instant as {@link #getDateTime()} but with the 'after' offset.
     *
     * @return the transition date-time expressed with the after offset, not null
     */
    public OffsetDateTime getDateTimeAfter() {
        return transitionAfter;
    }

    /**
     * Gets the offset before the gap.
     *
     * @return the offset before the gap, not null
     */
    public ZoneOffset getOffsetBefore() {
        return transition.getOffset();
    }

    /**
     * Gets the offset after the gap.
     *
     * @return the offset after the gap, not null
     */
    public ZoneOffset getOffsetAfter() {
        return transitionAfter.getOffset();
    }

    /**
     * Gets the size of the transition.
     *
     * @return the size of the transition, positive for gaps, negative for overlaps
     */
    public Period getTransitionSize() {
        int secs = getOffsetAfter().getAmountSeconds() - getOffsetBefore().getAmountSeconds();
        return Period.ofSeconds(secs).normalized();
    }

    /**
     * Does this transition represent a gap in the local time-line.
     *
     * @return true if this transition is a gap
     */
    public boolean isGap() {
        return getOffsetAfter().getAmountSeconds() > getOffsetBefore().getAmountSeconds();
    }

    /**
     * Does this transition represent a gap in the local time-line.
     *
     * @return true if this transition is an overlap
     */
    public boolean isOverlap() {
        return getOffsetAfter().getAmountSeconds() < getOffsetBefore().getAmountSeconds();
    }

    /**
     * Checks if the specified offset is valid during this transition.
     * A gap will always return false.
     * An overlap will return true if the offset is either the before or after offset.
     *
     * @param offset  the offset to check, null returns false
     * @return true if the offset is valid during the transition
     */
    public boolean isValidOffset(ZoneOffset offset) {
        return isGap() ? false : (getOffsetBefore().equals(offset) || getOffsetAfter().equals(offset));
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this transition to another based on the transition instant.
     * The offsets are ignored, making this order inconsistent with equals.
     *
     * @param transition  the transition to compare to, not null
     * @return the comparator value, negative if less, positive if greater
     */
    public int compareTo(ZoneOffsetTransition transition) {
        return this.getInstant().compareTo(transition.getInstant());
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if this object equals another.
     * <p>
     * The entire state of the object is compared.
     *
     * @param other  the other object to compare to, null returns false
     * @return true if equal
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (other instanceof ZoneOffsetTransition) {
            ZoneOffsetTransition d = (ZoneOffsetTransition) other;
            return transition.equals(d.transition) &&
                transitionAfter.getOffset().equals(d.transitionAfter.getOffset());
        }
        return false;
    }

    /**
     * Returns a suitable hash code.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return transition.hashCode() ^ transitionAfter.getOffset().hashCode();
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a string describing this object.
     *
     * @return a string for debugging, never null
     */
    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("Transition[")
            .append(isGap() ? "Gap" : "Overlap")
            .append(" at ")
            .append(transition)
            .append(" to ")
            .append(transitionAfter.getOffset())
            .append(']');
        return buf.toString();
    }

}
