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
package javax.time.calendar;

import java.io.Serializable;

import javax.time.MathUtils;
import javax.time.period.PeriodView;

/**
 * A time of day to nanosecond precision.
 * <p>
 * TimeHMSN is an immutable moment that records time information without a date.
 * For example, the value "14:30:04.123456789" can be stored in a TimeHMSN.
 * <p>
 * Static factory methods allow you to constuct instances.
 * <p>
 * TimeHMSN is thread-safe and immutable.
 *
 * @author Stephen Colebourne
 */
public final class TimeHMSN
        implements TimeCalendrical, Comparable<TimeHMSN>, Serializable {

    /**
     * A serialization identifier for this instance.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The nanosecond within the day that this TimeHMSN represents.
     */
    private final long nanoOfDay;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>TimeHMSN</code>.
     *
     * @param hourOfDay  the hour of day to represent
     * @param minuteOfHour  the minute of hour to represent
     * @param secondAndNanosOfMinute  the second of minute with nanoseconds to represent
     * @return a TimeHMSN object representing the specified time
     */
    public static TimeHMSN timeOfDay(int hourOfDay, int minuteOfHour, double secondAndNanosOfMinute) {
        return new TimeHMSN(0);
    }

    /**
     * Obtains an instance of <code>TimeHMSN</code>.
     *
     * @param hourOfDay  the hour of day to represent
     * @param minuteOfHour  the minute of hour to represent
     * @param secondOfMinute  the second of minute to represent
     * @param nanoOfSecond  the nano of second to represent
     * @return a TimeHMSN object representing the specified time
     */
    public static TimeHMSN timeOfDay(int hourOfDay, int minuteOfHour, int secondOfMinute, int nanoOfSecond) {
        return new TimeHMSN(0);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance with the specified nano of second.
     *
     * @param nanoOfDay  the nano of day to represent
     */
    private TimeHMSN(long nanoOfDay) {
        this.nanoOfDay = nanoOfDay;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the calendrical state which provides internal access to this
     * TimeHMSN instance.
     *
     * @return the calendar state for this instance, never null
     */
    @Override
    public CalendricalState getCalendricalState() {
        return null;  // TODO
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the hour of day value.
     *
     * @return the hour of day
     */
    public int getHourOfDay() {
        return 0;
    }

    /**
     * Gets the minute of hour value.
     *
     * @return the minute of hour
     */
    public int getMinuteOfHour() {
        return 0;
    }

    /**
     * Gets the second of minute value.
     *
     * @return the second of minute
     */
    public int getSecondOfMinute() {
        return 0;
    }

    /**
     * Gets the nano of second value.
     *
     * @return the nano of second
     */
    public int getNanoOfSecond() {
        return 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this TimeHMSN with the specified values altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param moment  the moment to update to, not null
     * @return a new updated TimeHMSN
     */
    public TimeHMSN with(Calendrical moment) {
        return null;
    }

    /**
     * Returns a copy of this TimeHMSN with the specified values altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param moments  the moments to update to, not null
     * @return a new updated TimeHMSN
     */
    public TimeHMSN with(Calendrical... moments) {
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this TimeHMSN with the hour of day value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hourOfDay  the hour of day to represent
     * @return a new updated TimeHMSN
     */
    public TimeHMSN withHourOfDay(int hourOfDay) {
        return new TimeHMSN(nanoOfDay);
    }

    /**
     * Returns a copy of this TimeHMSN with the minute of hour value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param minuteOfHour  the minute of hour to represent
     * @return a new updated TimeHMSN
     */
    public TimeHMSN withMinuteOfHour(int minuteOfHour) {
        return new TimeHMSN(nanoOfDay);
    }

    /**
     * Returns a copy of this TimeHMSN with the second of minute value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param secondOfMinute  the second of minute to represent
     * @return a new updated TimeHMSN
     */
    public TimeHMSN withSecondOfMinute(int secondOfMinute) {
        return new TimeHMSN(nanoOfDay);
    }

    /**
     * Returns a copy of this TimeHMSN with the nano of second value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param nanoOfSecond  the nano of second to represent
     * @return a new updated TimeHMSN
     */
    public TimeHMSN withNanoOfSecond(int nanoOfSecond) {
        return new TimeHMSN(nanoOfDay);
    }

    /**
     * Returns a copy of this TimeHMSN with the second and nanos values altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param secondAndNanosOfMinute  the second of minute with nanos to represent
     * @return a new updated TimeHMSN
     */
    public TimeHMSN withSecondOfMinute(double secondAndNanosOfMinute) {
        return new TimeHMSN(nanoOfDay);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this TimeHMSN with the specified period added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param period  the period to add, not null
     * @return a new updated TimeHMSN
     */
    public TimeHMSN plus(PeriodView period) {
        // TODO
        return null;
    }

    /**
     * Returns a copy of this TimeHMSN with the specified periods added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param periods  the periods to add, not null
     * @return a new updated TimeHMSN
     */
    public TimeHMSN plus(PeriodView... periods) {
        // TODO
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this TimeHMSN with the specified number of hours added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hours  the hours to add
     * @return a new updated TimeHMSN
     */
    public TimeHMSN plusHours(int hours) {
        return new TimeHMSN(nanoOfDay);
    }

    /**
     * Returns a copy of this TimeHMSN with the specified number of minutes added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param minutes  the minutes to add
     * @return a new updated TimeHMSN
     */
    public TimeHMSN plusMinutes(int minutes) {
        return new TimeHMSN(nanoOfDay);
    }

    /**
     * Returns a copy of this TimeHMSN with the specified number of seconds added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param seconds  the seconds to add
     * @return a new updated TimeHMSN
     */
    public TimeHMSN plusSeconds(int seconds) {
        return new TimeHMSN(nanoOfDay);
    }

    /**
     * Returns a copy of this TimeHMSN with the specified number of nanoseconds added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param nanos  the nanosseconds to add
     * @return a new updated TimeHMSN
     */
    public TimeHMSN plusNanos(int nanos) {
        return new TimeHMSN(nanoOfDay);
    }

    /**
     * Returns a copy of this TimeHMSN with the specified number of seconds added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param secondsAndNanos  the secondsAndNanos to add
     * @return a new updated TimeHMSN
     */
    public TimeHMSN plusSeconds(double secondsAndNanos) {
        return new TimeHMSN(nanoOfDay);
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this instance to another.
     *
     * @param otherTimeHMSN  the other instance, not null
     * @return the comparator value, negative if less, postive if greater
     * @throws NullPointerException if other value is null
     */
    public int compareTo(TimeHMSN otherTimeHMSN) {
        return MathUtils.safeCompare(nanoOfDay, otherTimeHMSN.nanoOfDay);
    }

    /**
     * Is this instance after the specified one.
     *
     * @param otherTimeHMSN  the other time of day instance, not null
     * @return true if this time of day is later
     * @throws NullPointerException if otherTimeHMSN is null
     */
    public boolean isAfter(TimeHMSN otherTimeHMSN) {
        return compareTo(otherTimeHMSN) > 0;
    }

    /**
     * Is this instance before the specified one.
     *
     * @param otherTimeHMSN  the other time of day instance, not null
     * @return true if this time of day is earlier
     * @throws NullPointerException if otherTimeHMSN is null
     */
    public boolean isBefore(TimeHMSN otherTimeHMSN) {
        return compareTo(otherTimeHMSN) < 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Is this instance equal to that specified.
     *
     * @param otherTimeHMSN  the other time of day instance, null returns false
     * @return true if the time of day is the same
     */
    @Override
    public boolean equals(Object otherTimeHMSN) {
        if (this == otherTimeHMSN) {
            return true;
        }
        if (otherTimeHMSN instanceof TimeHMSN) {
            return this.nanoOfDay == ((TimeHMSN) otherTimeHMSN).nanoOfDay;
        }
        return false;
    }

    /**
     * A hashcode for the time of day object.
     *
     * @return a suitable hashcode
     */
    @Override
    public int hashCode() {
        return (int) (nanoOfDay >>> 32 * nanoOfDay);
    }

}
