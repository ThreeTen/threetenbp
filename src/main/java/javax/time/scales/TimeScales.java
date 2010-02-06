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
package javax.time.scales;

/**
 * Provides common implementations of {@code TimeScale}.
 * <p>
 * TimeScales is a utility class.
 * All time-scales returned are {@code Serializable}, immutable and thread-safe.
 *
 * @author Stephen Colebourne
 */
public final class TimeScales {

    /**
     * Private constructor since this is a utility class
     */
    private TimeScales() {
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains a {@code TimeScale} instance representing the internationally agreed
     * TAI (International Atomic Time) time-scale.
     * <p>
     * This time-scale is the primary international scientific time-scale
     * and is completely continuous and unambiguous.
     *
     * @return the TAI time-scale, never null
     */
    public static TimeScale tai() {
        return TAI.INSTANCE;
    }

    /**
     * Obtains a {@code TimeScale} instance representing the "simplified UTC"
     * time-scale frequently used in computing which ignore leap seconds.
     * <p>
     * The internationally agreed UTC time-scale uses "leap seconds" to ensure that
     * the instant represented stays in line with the earths rotational changes.
     * These leap seconds are hard to follow and calculate with, thus many computer
     * systems simply pretend they do not exist.
     * <p>
     * This time-scale has no internationally recognized name, but is often incorrectly
     * referred to as UTC. The Java Time Framework refers to this time-scale as
     * "simplified UTC".
     * <p>
     * The result of ignoring leap seconds is that real leap seconds cannot be represented.
     * The benefit is that a day always has a fixed length of 86400 seconds.
     * The downside is that the time-scale does not model physical reality.
     * This is not a problem for most applications, but may be a problem for some.
     * If the application needs to handle leap seconds then the true UTC or TAI
     * time-scale should be used instead.
     * <p>
     * This class does correctly implement the relationship between TAI and UTC in
     * the period 1961 to 1972 (and also extends that to the period 1958 - 1961).
     * Prior to 1958 UTC, TrueUTC and TAI are defined to be identical.
     * 
     * UTC without leap seconds.
     * UTC as most commonly implemented in computer systems.
     *
     * @return
     */
    public static TimeScale simplifiedUtc() {
        return UTC.INSTANCE;
    }

    /**
     * Obtains a {@code TimeScale} instance representing the internationally agreed
     * UTC (Universal Coordinated Time) time-scale which includes leap seconds.
     * <p>
     * The true UTC time-scale differs from the TAI time-scale by an integral
     * number of seconds (although see below for a historical note).
     * These "leap seconds" are introduced to ensure that the instant represented
     * by UTC stays in line with the earths rotational changes.
     * <p>
     * Unlike many software implementations of UTC, this time-scale fully supports leap seconds.
     * It correctly implements the relationship between TAI and UTC between 1961 and 1972
     * where the UTC second did not correspond to an SI second and there were
     * a number of small discontinuities (of 50ms or 100ms). This has been further
     * extended back to 1958 where UT and TAI where equal. In the period 1958 to
     * 1961 adjustments where not coordinated internationally, hence there is no
     * single definitive record of the relationship between TAI and UT.
     * 
     * True UTC with leap seconds.
     * UTC with support for leap seconds.
     *
     * @return
     */
    public static TimeScale trueUtc() {
        return TrueUTC.INSTANCE;
    }

}
