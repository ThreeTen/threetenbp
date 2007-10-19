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
 * A time of day to minute precision.
 * <p>
 * TimeHM is an immutable calendar object that records time information
 * without a date to minute precision.
 * For example, the value "14:30" can be stored in a TimeHM.
 * <p>
 * Static factory methods allow you to constuct instances.
 * <p>
 * TimeHM is thread-safe and immutable.
 *
 * @author Stephen Colebourne
 */
public final class TimeHM
        implements Calendrical, Comparable<TimeHM>, Serializable {

    /**
     * A serialization identifier for this instance.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The minute within the day that this TimeHM represents.
     */
    private final int minuteOfDay;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>TimeHM</code>.
     *
     * @param hourOfDay  the hour of day to represent
     * @param minuteOfHour  the minute of hour to represent
     * @return a TimeHM object representing the specified time
     */
    public static TimeHM timeOfDay(int hourOfDay, int minuteOfHour) {
        return new TimeHM(0);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance with the specified month of year.
     *
     * @param minuteOfDay  the minute of day to represent
     */
    private TimeHM(int minuteOfDay) {
        this.minuteOfDay = minuteOfDay;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the calendrical state which provides internal access to this
     * TimeHM instance.
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

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this TimeHM with the specified values altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param moment  the moment to update to, not null
     * @return a new updated TimeHM
     */
    public TimeHM with(Calendrical moment) {
        return null;
    }

    /**
     * Returns a copy of this TimeHM with the specified values altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param calendricals  the calendricals to update to, not null
     * @return a new updated TimeHM
     */
    public TimeHM with(Calendrical... calendricals) {
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this TimeHM with the hour of day value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hourOfDay  the hour of day to represent
     * @return a new updated TimeHM
     */
    public TimeHM withHourOfDay(int hourOfDay) {
        return new TimeHM(minuteOfDay);
    }

    /**
     * Returns a copy of this TimeHM with the hour of day value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param minuteOfHour  the minute of hour to represent
     * @return a new updated TimeHM
     */
    public TimeHM withMinuteOfHour(int minuteOfHour) {
        return new TimeHM(minuteOfDay);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this TimeHM with the specified period added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param period  the period to add, not null
     * @return a new updated TimeHM
     */
    public TimeHM plus(PeriodView period) {
        // TODO
        return null;
    }

    /**
     * Returns a copy of this TimeHM with the specified periods added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param periods  the periods to add, not null
     * @return a new updated TimeHM
     */
    public TimeHM plus(PeriodView... periods) {
        // TODO
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this TimeHM with the specified number of hours added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hours  the hours to add
     * @return a new updated TimeHM
     */
    public TimeHM plusHours(int hours) {
        return new TimeHM(minuteOfDay);
    }

    /**
     * Returns a copy of this TimeHM with the specified number of minutes added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param minutes  the minutes to add
     * @return a new updated TimeHM
     */
    public TimeHM plusMinutes(int minutes) {
        return new TimeHM(minuteOfDay);
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this instance to another.
     *
     * @param otherTimeHM  the other instance, not null
     * @return the comparator value, negative if less, postive if greater
     * @throws NullPointerException if other value is null
     */
    public int compareTo(TimeHM otherTimeHM) {
        if (this.minuteOfDay < otherTimeHM.minuteOfDay) {
            return -1;
        }
        if (this.minuteOfDay > otherTimeHM.minuteOfDay) {
            return 1;
        }
        return 0;
    }

    /**
     * Is this instance after the specified one.
     *
     * @param otherTimeHM  the other time of day instance, not null
     * @return true if this time of day is later
     * @throws NullPointerException if otherTimeHM is null
     */
    public boolean isAfter(TimeHM otherTimeHM) {
        return compareTo(otherTimeHM) > 0;
    }

    /**
     * Is this instance before the specified one.
     *
     * @param otherTimeHM  the other time of day instance, not null
     * @return true if this time of day is earlier
     * @throws NullPointerException if otherTimeHM is null
     */
    public boolean isBefore(TimeHM otherTimeHM) {
        return compareTo(otherTimeHM) < 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Is this instance equal to that specified.
     *
     * @param otherTimeHM  the other time of day instance, null returns false
     * @return true if the time of day is the same
     */
    @Override
    public boolean equals(Object otherTimeHM) {
        if (this == otherTimeHM) {
            return true;
        }
        if (otherTimeHM instanceof TimeHM) {
            return this.minuteOfDay == ((TimeHM) otherTimeHM).minuteOfDay;
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
        return minuteOfDay;
    }

}
