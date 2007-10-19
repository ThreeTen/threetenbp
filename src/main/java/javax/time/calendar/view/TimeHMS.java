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
package javax.time.calendar.view;

import java.io.Serializable;

import javax.time.calendar.Calendrical;
import javax.time.calendar.CalendricalState;
import javax.time.period.PeriodView;

/**
 * A time of day to second precision.
 * <p>
 * TimeHMS is an immutable moment that records time information without a date.
 * For example, the value "14:30:04" can be stored in a TimeHMS.
 * <p>
 * Static factory methods allow you to constuct instances.
 * <p>
 * TimeHMS is thread-safe and immutable.
 *
 * @author Stephen Colebourne
 */
public final class TimeHMS
        implements Calendrical, Comparable<TimeHMS>, Serializable {

    /**
     * A serialization identifier for this instance.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The second within the day that this TimeHMS represents.
     */
    private final int secondOfDay;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>TimeHMS</code>.
     *
     * @param hourOfDay  the hour of day to represent
     * @param minuteOfHour  the minute of hour to represent
     * @param secondOfMinute  the second of minute to represent
     * @return a TimeHMS object representing the specified time
     */
    public static TimeHMS timeOfDay(int hourOfDay, int minuteOfHour, int secondOfMinute) {
        return new TimeHMS(0);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance with the specified month of year.
     *
     * @param secondOfDay  the second of day to represent
     */
    private TimeHMS(int secondOfDay) {
        this.secondOfDay = secondOfDay;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the calendrical state which provides internal access to this
     * TimeHMS instance.
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
     * Returns a copy of this TimeHMS with the specified values altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param moment  the moment to update to, not null
     * @return a new updated TimeHMS
     */
    public TimeHMS with(Calendrical moment) {
        return null;
    }

    /**
     * Returns a copy of this TimeHMS with the specified values altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param moments  the moments to update to, not null
     * @return a new updated TimeHMS
     */
    public TimeHMS with(Calendrical... moments) {
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this TimeHMS with the hour of day value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hourOfDay  the hour of day to represent
     * @return a new updated TimeHMS
     */
    public TimeHMS withHourOfDay(int hourOfDay) {
        return new TimeHMS(secondOfDay);
    }

    /**
     * Returns a copy of this TimeHMS with the hour of day value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param minuteOfHour  the minute of hour to represent
     * @return a new updated TimeHMS
     */
    public TimeHMS withMinuteOfHour(int minuteOfHour) {
        return new TimeHMS(secondOfDay);
    }

    /**
     * Returns a copy of this TimeHMS with the hour of day value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param secondOfMinute  the second of minute to represent
     * @return a new updated TimeHMS
     */
    public TimeHMS withSecondOfMinute(int secondOfMinute) {
        return new TimeHMS(secondOfDay);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this TimeHMS with the specified period added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param period  the period to add, not null
     * @return a new updated TimeHMS
     */
    public TimeHMS plus(PeriodView period) {
        // TODO
        return null;
    }

    /**
     * Returns a copy of this TimeHMS with the specified periods added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param periods  the periods to add, not null
     * @return a new updated TimeHMS
     */
    public TimeHMS plus(PeriodView... periods) {
        // TODO
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this TimeHMS with the specified number of hours added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hours  the hours to add
     * @return a new updated TimeHMS
     */
    public TimeHMS plusHours(int hours) {
        return new TimeHMS(secondOfDay);
    }

    /**
     * Returns a copy of this TimeHMS with the specified number of minutes added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param minutes  the minutes to add
     * @return a new updated TimeHMS
     */
    public TimeHMS plusMinutes(int minutes) {
        return new TimeHMS(secondOfDay);
    }

    /**
     * Returns a copy of this TimeHMS with the specified number of seconds added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param seconds  the secondsto add
     * @return a new updated TimeHMS
     */
    public TimeHMS plusSeconds(int seconds) {
        return new TimeHMS(secondOfDay);
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this instance to another.
     *
     * @param otherTimeHMS  the other instance, not null
     * @return the comparator value, negative if less, postive if greater
     * @throws NullPointerException if other value is null
     */
    public int compareTo(TimeHMS otherTimeHMS) {
        if (this.secondOfDay < otherTimeHMS.secondOfDay) {
            return -1;
        }
        if (this.secondOfDay > otherTimeHMS.secondOfDay) {
            return 1;
        }
        return 0;
    }

    /**
     * Is this instance after the specified one.
     *
     * @param otherTimeHMS  the other time of day instance, not null
     * @return true if this time of day is later
     * @throws NullPointerException if otherTimeHMS is null
     */
    public boolean isAfter(TimeHMS otherTimeHMS) {
        return compareTo(otherTimeHMS) > 0;
    }

    /**
     * Is this instance before the specified one.
     *
     * @param otherTimeHMS  the other time of day instance, not null
     * @return true if this time of day is earlier
     * @throws NullPointerException if otherTimeHMS is null
     */
    public boolean isBefore(TimeHMS otherTimeHMS) {
        return compareTo(otherTimeHMS) < 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Is this instance equal to that specified.
     *
     * @param otherTimeHMS  the other time of day instance, null returns false
     * @return true if the time of day is the same
     */
    @Override
    public boolean equals(Object otherTimeHMS) {
        if (this == otherTimeHMS) {
            return true;
        }
        if (otherTimeHMS instanceof TimeHMS) {
            return this.secondOfDay == ((TimeHMS) otherTimeHMS).secondOfDay;
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
