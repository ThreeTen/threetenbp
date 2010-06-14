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

import java.io.Serializable;
import java.util.Arrays;
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
 *
 * @author Stephen Colebourne
 */
public abstract class LeapSecondRules implements Serializable {

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Constant for seconds per day.
     */
    private static final int SECS_PER_DAY = 24 * 60 * 60;
    /**
     * Constant for the offset from MJD day 0 to TAI day 0.
     */
    private static final int OFFSET_MJD_TAI = 36204;
    /**
     * Constant for nanos per second.
     */
    private static final long NANOS_PER_SECOND = 1000000000;

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
     * The default conversion is performed using {@link #getTAIOffset(long)}.
     *
     * @return the converted TAI instant, not null
     */
    public TAIInstant convertToTAI(UTCInstant utcInstant) {
        long mjd = utcInstant.getModifiedJulianDay();
        long nod = utcInstant.getNanoOfDay();
        long taiSecs = (mjd - OFFSET_MJD_TAI) * SECS_PER_DAY + nod / NANOS_PER_SECOND + getTAIOffset(mjd);
        int nos = (int) (nod % NANOS_PER_SECOND);
        return TAIInstant.ofTAISeconds(taiSecs, nos);
    }

    /**
     * Converts a {@code TAIInstant} to a {@code UTCInstant}.
     * <p>
     * The default conversion is performed using {@link #getTAIOffset(long)}.
     *
     * @return the converted UTC instant, not null
     */
    public UTCInstant convertToUTC(TAIInstant taiInstant) {
        long[] mjds = getLeapSecondDates();
        TAIInstant[] tais = new TAIInstant[mjds.length];
        for (int i = 0; i < mjds.length; i++) {
            long nod = (SECS_PER_DAY + getLeapSecondAdjustment(mjds[i])) * NANOS_PER_SECOND - 1;
            UTCInstant utc = UTCInstant.ofModifiedJulianDay(mjds[i], nod);
            tais[i] = convertToTAI(utc);
        }
        int pos = Arrays.binarySearch(tais, taiInstant);
        pos = (pos < 0 ? -(pos + 1) : pos);
//        TAIInstant regionStart = tais[pos];
        long mjdRegionStart = (pos > 0 ? mjds[pos - 1] : Long.MIN_VALUE);
        int taiOffset = getTAIOffset(mjdRegionStart);
        long taiSecs = taiInstant.getTAISeconds();
        long utcSecsTaiEpoch = taiSecs - taiOffset;
        long mjd = MathUtils.floorDiv(utcSecsTaiEpoch, SECS_PER_DAY) + OFFSET_MJD_TAI;
        long nod = MathUtils.floorMod(taiSecs, SECS_PER_DAY) * NANOS_PER_SECOND + taiInstant.getNanoOfSecond();
        return UTCInstant.ofModifiedJulianDay(mjd, nod, this);
        
//        long taiSecs = taiInstant.getTAISeconds();
//        long mjDayEst = MathUtils.floorDiv(taiSecs, SECS_PER_DAY);
//        mjDayEst = (taiSecs - getTAIOffset(mjDayEst)) / SECS_PER_DAY;
//        long mjDay = mjDayEst + OFFSET_MJD_TAI;
//        long nod = MathUtils.floorMod(taiSecs, SECS_PER_DAY) * NANOS_PER_SECOND + taiInstant.getNanoOfSecond();
//        return UTCInstant.ofModifiedJulianDay(mjDay, nod, this);
    }

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
