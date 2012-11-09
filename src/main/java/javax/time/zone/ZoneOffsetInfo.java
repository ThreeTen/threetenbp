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

import javax.time.ZoneOffset;

/**
 * Information about the offset applicable for a local date-time.
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
 * These cases are modeled by implementations of this interface.
 * The first case is modeled by {@link ZoneOffset}, the second two by {@link ZoneOffsetTransition}.
 * Applications must use {@code instanceof} to determine which case applies.
 * Alternatively use the general purpose {@link #isValidOffset(ZoneOffset)}.
 *
 * <h4>Implementation notes</h4>
 * This interface must not be implemented by application code.
 * The only permitted implementations are {@code ZoneOffset} and {@code ZoneOffsetTransition}.
 * Both these implementations are immutable and thread-safe.
 */
public interface ZoneOffsetInfo {

    /**
     * Checks if the specified offset is valid for the date-time that
     * was queried to obtain this information.
     * <p>
     * This method returns {@code true} if the specified offset is one of the
     * valid offsets defined by the implementing class.
     * <p>
     * If the queried date-time is not in a transition (the normal case), this
     * {@code ZoneOffsetInfo} will be a {@code ZoneOffset}.
     * The only valid offset is therefore the same offset as {@code this}.
     * <p>
     * If the queried date-time is in a transition, this {@code ZoneOffsetInfo}
     * will be a {@code ZoneOffsetTransition}.
     * During a gap, there will be no valid offsets.
     * During an overlap, there will be two valid offsets, before and after.
     *
     * @param offset  the offset to check, null returns false
     * @return true if the offset is one of those allowed by the date-time
     */
    boolean isValidOffset(ZoneOffset offset);

}
