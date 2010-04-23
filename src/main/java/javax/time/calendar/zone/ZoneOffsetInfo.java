/*
 * Copyright (c) 2007-2010, Stephen Colebourne & Michael Nascimento Santos
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

import javax.time.calendar.LocalDateTime;
import javax.time.calendar.OffsetDateTime;
import javax.time.calendar.ZoneOffset;

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
 * <p>
 * OffsetInfo is immutable and thread-safe.
 *
 * @author Stephen Colebourne
 */
public final class ZoneOffsetInfo {

    /**
     * The date-time that this info applies to.
     */
    private final LocalDateTime dateTime;
    /**
     * The offset for the local time-line.
     */
    private final ZoneOffset offset;
    /**
     * The transition between two offsets on the local time-line.
     */
    private final ZoneOffsetTransition transition;

    /**
     * Constructor for handling a simple single offset.
     *
     * @param dateTime  the date-time that this info applies to, not null
     * @param offset  the offset applicable at the date-time, not null
     */
    ZoneOffsetInfo(
            LocalDateTime dateTime,
            ZoneOffset offset) {
        this.dateTime = dateTime;
        this.offset = offset;
        this.transition = null;
    }

    /**
     * Constructor for handling a transition.
     *
     * @param dateTime  the date-time that this info applies to, not null
     * @param cutoverDateTime  the date-time of the cutover with the offset before, not null
     * @param offsetAfter  the offset applicable after the cutover gap/overlap, not null
     */
    ZoneOffsetInfo(
            LocalDateTime dateTime,
            OffsetDateTime cutoverDateTime,
            ZoneOffset offsetAfter) {
        this.dateTime = dateTime;
        this.offset = null;
        this.transition = new ZoneOffsetTransition(cutoverDateTime, offsetAfter);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the local date-time that this info is applicable to.
     *
     * @return the date-time that this is the information for, not null
     */
    public LocalDateTime getLocalDateTime() {
        return dateTime;
    }

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
     * This method is intended for use when {@link #isTransition()} returns {@code false}.
     *
     * @return the offset applicable when there is not a transition on the
     *  local-time line, null if it is a transition
     */
    public ZoneOffset getOffset() {
        return offset;
    }

    /**
     * Gets information about the transition occurring on the local time-line.
     * <p>
     * This method is intended for use when {@link #isTransition()} returns {@code true}
     *
     * @return the transition on the local-time line, null if not a transition
     */
    public ZoneOffsetTransition getTransition() {
        return transition;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets an estimated offset for the local date-time.
     * <p>
     * The date-time will typically have a single valid offset.
     * During a gap, there will be no valid offsets.
     * During an overlap, there will be two valid offsets.
     * This method returns the {@link #getOffset() single offset} in the normal
     * case, and the {@link ZoneOffsetTransition#getOffsetAfter() offset after}
     * when a transition is occurring.
     *
     * @return a suitable estimated offset, never null
     */
    public ZoneOffset getEstimatedOffset() {
        return isTransition() ? getTransition().getOffsetAfter() : offset;
    }

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
     * Is this instance equal to that specified by comparing the offset.
     *
     * @param otherInfo  the other info, null returns false
     * @return true if this rules is the same as that specified
     */
    @Override
    public boolean equals(Object otherInfo) {
        if (this == otherInfo) {
           return true;
        }
        if (otherInfo instanceof ZoneOffsetInfo) {
            ZoneOffsetInfo info = (ZoneOffsetInfo) otherInfo;
            return dateTime.equals(info.dateTime) &&
                    (transition != null ? transition.equals(info.transition) : offset.equals(info.offset));
        }
        return false;
    }

    /**
     * A hash code for the info object.
     *
     * @return a suitable hash code
     */
    @Override
    public int hashCode() {
        return dateTime.hashCode() ^ (transition != null ? transition.hashCode() : offset.hashCode());
    }

    /**
     * Gets a string describing this object.
     *
     * @return a string for debugging, never null
     */
    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("OffsetInfo[")
            .append(dateTime)
            .append(':')
            .append(isTransition() ? transition : offset)
            .append(']');
        return buf.toString();
    }

}
