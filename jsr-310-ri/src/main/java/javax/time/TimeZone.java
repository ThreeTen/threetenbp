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

import javax.xml.datatype.Duration;

/**
 * A time zone representing the set of rules by which the time offset from
 * UTC varies through the year and historically.
 * <p>
 * All supplied subclasses of TimeZone are thread-safe and immutable.
 * Other subclasses of TimeZone should be immutable but this cannot be enforced.
 *
 * @author Stephen Colebourne
 */
public abstract class TimeZone {

    /** The time zone ID. */
    private final String timeZoneID;

    /**
     * Obtains an instance of <code>TimeZone</code> using its ID.
     *
     * @param timeZoneID  the time zone id, not null
     * @return the TimeZone
     */
    public static TimeZone timeZone(String timeZoneID) {
        return null;
    }

    /**
     * Obtains an instance of <code>TimeZone</code> using an offset in hours.
     *
     * @param hoursOffset  the time zone offset in hours
     * @return the TimeZone
     */
    public static TimeZone timeZoneOffset(int hoursOffset) {
        return null;
    }

    /**
     * Obtains an instance of <code>TimeZone</code> using an offset in hours.
     *
     * @param hoursOffset  the time zone offset in hours
     * @param minutesOffset  the time zone offset in minutes (0-59)
     * @return the TimeZone
     */
    public static TimeZone timeZoneOffset(int hoursOffset, int minutesOffset) {
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance using the time zone ID.
     *
     * @param timeZoneID  the time zone id, not null
     */
    private TimeZone(String timeZoneID) {
        super();
        this.timeZoneID = timeZoneID;
    }

    /**
     * Resolves singletons.
     *
     * @return the singleton instance
     */
    private Object readResolve() {
        return TimeZone.timeZone(timeZoneID);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the time zone ID.
     *
     * @return the time zone ID, never null
     */
    public String getID() {
        return timeZoneID;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the name of the time zone.
     *
     * @return the time zone name, never null
     */
    public String getName() {
        return timeZoneID;
    }

    /**
     * Gets the short name of the time zone.
     *
     * @return the time zone short name, never null
     */
    public String getShortName() {
        return timeZoneID;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the offset to add to the specified instant to get local time.
     *
     * @param instant  the instant to find the offset for, not null
     * @return the millisecond offset to add to UTC to get local time
     */
    public abstract Duration getOffset(Instant instant);

    /**
     * Gets the offset to add to the specified instant to get local time.
     *
     * @return true if the time zone is fixed and the offset never changes
     */
    public abstract boolean isFixed();

    //-----------------------------------------------------------------------
    /**
     * Is this instance equal to that specified by comparing the ID.
     *
     * @param otherZone  the other zone, null returns false
     * @return true if this zone is the same as that specified
     */
    @Override
    public boolean equals(Object otherZone) {
        if (this == otherZone) {
           return true;
        }
        if (otherZone instanceof TimeZone) {
            return timeZoneID.equals(((TimeZone) otherZone).timeZoneID);
        }
        return false;
    }

    /**
     * A hashcode for the years object.
     *
     * @return a suitable hashcode
     */
    @Override
    public int hashCode() {
        return timeZoneID.hashCode();
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a string representation of the time zone using the ID.
     *
     * @return the number of years in ISO8601 string format
     */
    @Override
    public String toString() {
        return timeZoneID;
    }

}
