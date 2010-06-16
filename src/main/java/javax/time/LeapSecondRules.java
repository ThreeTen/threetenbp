/*
 * Copyright (c) 2010, Stephen Colebourne & Michael Nascimento Santos
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

import java.util.ConcurrentModificationException;

/**
 * Rules defining when leap seconds occur.
 * <p>
 * This class defines the rules for when leap seconds occur.
 * Subclasses obtain the data from a suitable source, such as TZDB or GPS.
 * <p>
 * The static methods on this class provide access to the system leap second rules.
 * These are used by default.
 * <p>
 * LeapSecondRules is an abstract class and must be implemented with care
 * to ensure other classes in the framework operate correctly.
 * All implementations must be final, immutable and thread-safe.
 * It is only intended that the abstract methods are overridden.
 * Subclasses should be Serializable wherever possible.
 *
 * @author Stephen Colebourne
 */
public abstract class LeapSecondRules {

    /**
     * Gets the system default leap second rules.
     * <p>
     * The returned rules will remain up to date, in a thread-safe manner, as new
     * leap seconds are added.
     *
     * @return the system rules, never null
     */
    public static LeapSecondRules system() {
        return SystemLeapSecondRules.INSTANCE;
    }

    /**
     * Adds a new leap second to the system default leap second rules.
     * <p>
     * This method registers a new leap second with the system leap second rules.
     * Once registered, there is no way to deregister the leap second.
     * <p>
     * All calculations will be affected immediately that the method is called.
     * Calling the method is thread-safe and its effects are visible in all threads.
     *
     * @param mjDay  the modified julian date that the leap second occurs at the end of
     * @param leapAdjustment  the leap seconds to add/remove at the end of the day, from -1 to 2, not 0
     * @throws IllegalArgumentException if the day is before the last known leap second
     * @throws ConcurrentModificationException if another thread updates the rules at the same time
     */
    public static void registerSystemLeapSecond(long mjDay, int leapAdjustment) {
        SystemLeapSecondRules.INSTANCE.registerLeapSecond(mjDay, leapAdjustment);
    }

    //-----------------------------------------------------------------------
    /**
     * Creates an instance of the rules.
     */
    protected LeapSecondRules() {
    }

    //-----------------------------------------------------------------------
    /**
     * The name of these rules.
     *
     * @return the name, never null
     */
    public abstract String getName();

    /**
     * Gets the leap second adjustment on the specified date.
     * <p>
     * While this is normally zero (no leap second) or one (one extra second),
     * it is within the UTC specification to support minus one and plus two.
     * Callers should be prepared to receive any of these values.
     * <p>
     * Any leap seconds are added to, or removed from, the end of the specified date.
     *
     * @param mjDay  the date as a Modified Julian Day (number of days from the epoch of 1858-11-17)
     * @return the number of seconds added, or removed, from the date, from -1 to 2
     */
    public abstract int getLeapSecondAdjustment(long mjDay);

    /**
     * Gets the offset to TAI on the specified date.
     * <p>
     * The TAI offset starts at 10 in 1972 and varies from then on based on the
     * dates of leap seconds.
     * The offset will apply for the whole of the date.
     *
     * @param mjDay  the date as a Modified Julian Day (number of days from the epoch of 1858-11-17)
     * @return the TAI offset in seconds
     */
    public abstract int getTAIOffset(long mjDay);

    /**
     * Gets all known leap second dates.
     * <p>
     * The dates are returned as Modified Julian Day values.
     * The leap second is added to, or removed from, the <i>end</i> of the specified dates.
     * The dates will be sorted from earliest to latest.
     *
     * @return an array of leap second dates expressed as Modified Julian Day values
     */
    public abstract long[] getLeapSecondDates();

    //-----------------------------------------------------------------------
    /**
     * Converts a {@code UTCInstant} to a {@code TAIInstant}.
     * <p>
     * This method converts from the UTC to the TAI time-scale using the
     * leap-second rules of the implementation.
     *
     * @param utcInstant  the UTC instant to convert, not null
     * @return the converted TAI instant, not null
     */
    public abstract TAIInstant convertToTAI(UTCInstant utcInstant);

    /**
     * Converts a {@code TAIInstant} to a {@code UTCInstant}.
     * <p>
     * This method converts from the TAI to the UTC time-scale using the
     * leap-second rules of the implementation.
     *
     * @param taiInstant  the TAI instant to convert, not null
     * @return the converted UTC instant, not null
     */
    public abstract UTCInstant convertToUTC(TAIInstant taiInstant);

    //-----------------------------------------------------------------------
    /**
     * A string representation of these rules.
     *
     * @return the string representation, never null
     */
    @Override
    public String toString() {
        return "LeapSecondRules[" + getName() + ']';
    }

}
