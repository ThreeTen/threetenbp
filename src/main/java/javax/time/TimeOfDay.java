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

import java.io.Serializable;

/**
 * Recurring moment representing the time of day.
 * <p>
 * TimeOfDay is an immutable moment that records time information without a date.
 * For example, the value "14:30:04" can be stored in a TimeOfDay.
 * <p>
 * Static factory methods allow you to constuct instances.
 * <p>
 * TimeOfDay is thread-safe and immutable.
 *
 * @author Stephen Colebourne
 */
public final class TimeOfDay implements Calendrical, Comparable<TimeOfDay>, Serializable {

    /**
     * A serialization identifier for this instance.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The second within the day that this TimeOfDay represents.
     */
    private final int secondOfDay;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>TimeOfDay</code>.
     *
     * @param hourOfDay  the hour of day to represent
     * @param minuteOfHour  the minute of hour to represent
     * @return a TimeOfDay object representing the specified time
     */
    public static TimeOfDay timeOfDay(int hourOfDay, int minuteOfHour) {
        return new TimeOfDay(0);
    }

    /**
     * Obtains an instance of <code>TimeOfDay</code>.
     *
     * @param hourOfDay  the hour of day to represent
     * @param minuteOfHour  the minute of hour to represent
     * @param secondOfMinute  the second of minute to represent
     * @return a TimeOfDay object representing the specified time
     */
    public static TimeOfDay timeOfDay(int hourOfDay, int minuteOfHour, int secondOfMinute) {
        return new TimeOfDay(0);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance with the specified month of year.
     *
     * @param secondOfDay  the second of day to represent
     */
    private TimeOfDay(int secondOfDay) {
        this.secondOfDay = secondOfDay;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the calendrical state which provides internal access to this
     * TimeOfDay instance.
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

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this TimeOfDay with the specified values altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param moment  the moment to update to, not null
     * @return a new updated TimeOfDay
     */
    public TimeOfDay with(Calendrical moment) {
        return null;
    }

    /**
     * Returns a copy of this TimeOfDay with the specified values altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param moments  the moments to update to, not null
     * @return a new updated TimeOfDay
     */
    public TimeOfDay with(Calendrical... moments) {
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this TimeOfDay with the hour of day value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hourOfDay  the hour of day to represent
     * @return a new updated TimeOfDay
     */
    public TimeOfDay withHourOfDay(int hourOfDay) {
        return new TimeOfDay(secondOfDay);
    }

    /**
     * Returns a copy of this TimeOfDay with the hour of day value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param minuteOfHour  the minute of hour to represent
     * @return a new updated TimeOfDay
     */
    public TimeOfDay withMinuteOfHour(int minuteOfHour) {
        return new TimeOfDay(secondOfDay);
    }

    /**
     * Returns a copy of this TimeOfDay with the hour of day value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param secondOfMinute  the second of minute to represent
     * @return a new updated TimeOfDay
     */
    public TimeOfDay withSecondOfMinute(int secondOfMinute) {
        return new TimeOfDay(secondOfDay);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this TimeOfDay with the specified period added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param period  the period to add, not null
     * @return a new updated TimeOfDay
     */
    public TimeOfDay plus(Durational period) {
        // TODO
        return null;
    }

    /**
     * Returns a copy of this TimeOfDay with the specified periods added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param periods  the periods to add, not null
     * @return a new updated TimeOfDay
     */
    public TimeOfDay plus(Durational... periods) {
        // TODO
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this TimeOfDay with the specified number of hours added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hours  the hours to add
     * @return a new updated TimeOfDay
     */
    public TimeOfDay plusHours(int hours) {
        return new TimeOfDay(secondOfDay);
    }

    /**
     * Returns a copy of this TimeOfDay with the specified number of minutes added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param minutes  the minutes to add
     * @return a new updated TimeOfDay
     */
    public TimeOfDay plusMinutes(int minutes) {
        return new TimeOfDay(secondOfDay);
    }

    /**
     * Returns a copy of this TimeOfDay with the specified number of seconds added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param seconds  the secondsto add
     * @return a new updated TimeOfDay
     */
    public TimeOfDay plusSeconds(int seconds) {
        return new TimeOfDay(secondOfDay);
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this instance to another.
     *
     * @param otherTimeOfDay  the other instance, not null
     * @return the comparator value, negative if less, postive if greater
     * @throws NullPointerException if other value is null
     */
    public int compareTo(TimeOfDay otherTimeOfDay) {
        if (this.secondOfDay < otherTimeOfDay.secondOfDay) {
            return -1;
        }
        if (this.secondOfDay > otherTimeOfDay.secondOfDay) {
            return 1;
        }
        return 0;
    }

    /**
     * Is this instance after the specified one.
     *
     * @param otherTimeOfDay  the other time of day instance, not null
     * @return true if this time of day is later
     * @throws NullPointerException if otherTimeOfDay is null
     */
    public boolean isAfter(TimeOfDay otherTimeOfDay) {
        return compareTo(otherTimeOfDay) > 0;
    }

    /**
     * Is this instance before the specified one.
     *
     * @param otherTimeOfDay  the other time of day instance, not null
     * @return true if this time of day is earlier
     * @throws NullPointerException if otherTimeOfDay is null
     */
    public boolean isBefore(TimeOfDay otherTimeOfDay) {
        return compareTo(otherTimeOfDay) < 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Is this instance equal to that specified.
     *
     * @param otherTimeOfDay  the other time of day instance, null returns false
     * @return true if the time of day is the same
     */
    @Override
    public boolean equals(Object otherTimeOfDay) {
        if (this == otherTimeOfDay) {
            return true;
        }
        if (otherTimeOfDay instanceof TimeOfDay) {
            return this.secondOfDay == ((TimeOfDay) otherTimeOfDay).secondOfDay;
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
        return secondOfDay;
    }

}
