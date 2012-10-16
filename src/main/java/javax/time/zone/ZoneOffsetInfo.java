/*
 * Copyright (c) 2007-2012, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time.zone;

import javax.time.DateTimeException;
import javax.time.DateTimes;
import javax.time.ZoneOffset;

/**
 * Information about the valid offsets applicable for a local date-time.
 * <p>
 * The mapping from a local date-time to an offset is not straightforward.
 * There are three cases:
 * <ul>
 * <li>Normal. Where there is a single offset for the local date-time.</li>
 * <li>Gap. Where there is a gap in the local time-line typically caused by the
 * spring cutover to daylight savings. There are no valid offsets within the gap</li>
 * <li>Overlap. Where there is a gap in the local time-line typically caused by the
 * autumn cutover from daylight savings. There are two valid offsets during the overlap.</li>
 * </ul>
 * When using this class, it is vital to check the {@link #isTransition()}
 * method to handle the gap and overlap. Alternatively use one of the general
 * methods {@link #getEstimatedOffset()} or {@link #isValidOffset(ZoneOffset)}.
 * 
 * <h4>Implementation notes</h4>
 * This class is immutable and thread-safe.
 */
public final class ZoneOffsetInfo {

    /**
     * The offset for the local time-line.
     */
    private final ZoneOffset offset;
    /**
     * The transition between two offsets on the local time-line.
     */
    private final ZoneOffsetTransition transition;

    /**
     * Creates an instance representing a simple single offset.
     * <p>
     * Applications should normally obtain an instance from {@link ZoneRules}.
     * This method is intended for use by implementors of {@code ZoneRules}.
     *
     * @param offset  the offset applicable at the implied local date-time, not null
     */
    public static ZoneOffsetInfo ofOffset(ZoneOffset offset) {
        DateTimes.checkNotNull(offset, "ZoneOffsetTransition must not be null");
        return new ZoneOffsetInfo(offset, null);
    }

    /**
     * Creates an instance representing a transition.
     * <p>
     * Applications should normally obtain an instance from {@link ZoneRules}.
     * This method is intended for use by implementors of {@code ZoneRules}.
     *
     * @param transition  the details of the transition including the offset before and after, not null
     */
    public static ZoneOffsetInfo ofTransition(ZoneOffsetTransition transition) {
        DateTimes.checkNotNull(transition, "ZoneOffsetTransition must not be null");
        return new ZoneOffsetInfo(null, transition);
    }

    /**
     * Creates an instance handling a simple single offset.
     *
     * @param dateTime  the local date-time that this info applies to, not null
     * @param offset  the offset applicable at the date-time, not null
     */
    ZoneOffsetInfo(ZoneOffset offset, ZoneOffsetTransition transition) {
        this.offset = offset;
        this.transition = transition;
    }

    //-----------------------------------------------------------------------
    /**
     * Is a transition occurring on the local time-line.
     * <p>
     * A transition may be a gap or overlap and is normally caused by
     * daylight savings cutover.
     *
     * @return true if there is a transition occurring on the local time-line,
     *  false if there is a single valid offset
     */
    public boolean isTransition() {
        return transition != null;
    }

    /**
     * Gets the offset applicable at this point on the local time-line.
     * <p>
     * This method is intended for use when {@link #isTransition()} returns {@code false}
     * and throws an exception if a transition is occurring.
     *
     * @return the offset applicable when there is not a transition on the local-time line, not null
     * @throws DateTimeException if a transition is occurring
     */
    public ZoneOffset getOffset() {
        if (offset == null) {
            throw new DateTimeException("ZoneOffsetInfo represents a transition");
        }
        return offset;
    }

    /**
     * Gets information about the transition occurring on the local time-line.
     * <p>
     * This method is intended for use when {@link #isTransition()} returns {@code true}
     * and throws an exception if no transition is occurring.
     *
     * @return the transition on the local-time line, not null
     * @throws DateTimeException if no transition is occurring
     */
    public ZoneOffsetTransition getTransition() {
        if (transition == null) {
            throw new DateTimeException("ZoneOffsetInfo represents a transition");
        }
        return transition;
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if the specified offset is valid for this date-time.
     * <p>
     * The date-time will typically have a single valid offset.
     * During a gap, there will be no valid offsets.
     * During an overlap, there will be two valid offsets.
     * This method returns {@code true} if the specified offset is one of the
     * valid offsets.
     *
     * @param offset  the offset to check, null returns false
     * @return true if the offset is one of those allowed by the date-time
     */
    public boolean isValidOffset(ZoneOffset offset) {
        return isTransition() ? transition.isValidOffset(offset) : this.offset.equals(offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if this object equals another.
     * <p>
     * The entire state of the object is compared.
     *
     * @param otherInfo  the other object to compare to, null returns false
     * @return true if equal
     */
    @Override
    public boolean equals(Object otherInfo) {
        if (this == otherInfo) {
           return true;
        }
        if (otherInfo instanceof ZoneOffsetInfo) {
            ZoneOffsetInfo info = (ZoneOffsetInfo) otherInfo;
            return (transition != null ? transition.equals(info.transition) : offset.equals(info.offset));
        }
        return false;
    }

    /**
     * A suitable hash code for this object.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return (transition != null ? transition.hashCode() : offset.hashCode());
    }

    /**
     * Returns a string describing this object.
     *
     * @return a string for debugging, not null
     */
    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder(32);
        buf.append("OffsetInfo[")
            .append(isTransition() ? transition : offset)
            .append(']');
        return buf.toString();
    }

}
