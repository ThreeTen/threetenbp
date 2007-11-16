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
package javax.time.calendar.field;

import java.io.Serializable;

import javax.time.calendar.Calendrical;
import javax.time.calendar.CalendricalState;
import javax.time.calendar.TimeFieldRule;

/**
 * A time field representing a hour of day.
 * <p>
 * HourOfDay is an immutable time field that can only store a hour of day.
 * It is a type-safe way of representing a hour of day in an application.
 * <p>
 * Static factory methods allow you to construct instances.
 * The hour of day may be queried using getHourOfDay().
 * <p>
 * HourOfDay is thread-safe and immutable.
 *
 * @author Stephen Colebourne
 */
public final class HourOfDay implements Calendrical, Comparable<HourOfDay>, Serializable {

    /**
     * The rule implementation that defines how the hour of day field operates.
     */
    public static final TimeFieldRule RULE = new Rule();
    /**
     * A serialization identifier for this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The hour of day being represented.
     */
    private final int hourOfDay;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>HourOfDay</code>.
     *
     * @param hourOfDay  the hour of day to represent
     * @return the created HourOfDay
     */
    public static HourOfDay hourOfDay(int hourOfDay) {
        return new HourOfDay(hourOfDay);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance with the specified hour of day.
     *
     * @param hourOfDay  the hour of day to represent
     */
    private HourOfDay(int hourOfDay) {
        this.hourOfDay = hourOfDay;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the hour of day value.
     *
     * @return the hour of day
     */
    public int getHourOfDay() {
        return hourOfDay;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the calendrical state which provides internal access to this
     * HourOfDay instance.
     *
     * @return the calendar state for this instance, never null
     */
    @Override
    public CalendricalState getCalendricalState() {
        return null;  // TODO
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this hour of day instance to another.
     *
     * @param otherHourOfDay  the other hour of day instance, not null
     * @return the comparator value, negative if less, postive if greater
     * @throws NullPointerException if otherHourOfDay is null
     */
    public int compareTo(HourOfDay otherHourOfDay) {
        int thisValue = this.hourOfDay;
        int otherValue = otherHourOfDay.hourOfDay;
        return (thisValue < otherValue ? -1 : (thisValue == otherValue ? 0 : 1));
    }

    /**
     * Is this hour of day instance greater than the specified hour of day.
     *
     * @param otherHourOfDay  the other hour of day instance, not null
     * @return true if this hour of day is greater
     * @throws NullPointerException if otherHourOfDay is null
     */
    public boolean isGreaterThan(HourOfDay otherHourOfDay) {
        return compareTo(otherHourOfDay) > 0;
    }

    /**
     * Is this hour of day instance less than the specified hour of day.
     *
     * @param otherHourOfDay  the other hour of day instance, not null
     * @return true if this hour of day is less
     * @throws NullPointerException if otherHourOfDay is null
     */
    public boolean isLessThan(HourOfDay otherHourOfDay) {
        return compareTo(otherHourOfDay) < 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Is this instance equal to that specified, evaluating the hour of day.
     *
     * @param otherHourOfDay  the other hour of day instance, null returns false
     * @return true if the hour of day is the same
     */
    @Override
    public boolean equals(Object otherHourOfDay) {
        if (this == otherHourOfDay) {
            return true;
        }
        if (otherHourOfDay instanceof HourOfDay) {
            return hourOfDay == ((HourOfDay) otherHourOfDay).hourOfDay;
        }
        return false;
    }

    /**
     * A hashcode for the hour of day object.
     *
     * @return a suitable hashcode
     */
    @Override
    public int hashCode() {
        return hourOfDay;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets whether it is AM or PM.
     * <p>
     * AM is defined as 00:00 to 11:59 inclusive.<br />
     * PM is defined as 12:00 to 23:59 inclusive.<br />
     *
     * @return true is the time is in the morning
     */
    public MeridianOfDay getAmPm() {
        return MeridianOfDay.meridianOfDay(hourOfDay / 12);
    }

    /**
     * Gets the hour of AM or PM, from 0 to 11.
     * <p>
     * This method returns the value from {@link #hourOfDay} modulo 12.
     * This is rarely used. The time as seen on clocks and watches is
     * returned from {@link #getClockHourOfAmPm()}.
     * <p>
     * The hour from 00:00 to 00:59 will return 0.<br />
     * The hour from 01:00 to 01:59 will return 1.<br />
     * The hour from 11:00 to 11:59 will return 11.<br />
     * The hour from 12:00 to 12:59 will return 0.<br />
     * The hour from 23:00 to 23:59 will return 11.<br />
     *
     * @return true is the time is in the morning
     */
    public int getHourOfAmPm() {
        return hourOfDay % 12;
    }

    /**
     * Gets the clock hour of AM or PM, from 1 to 12.
     * <p>
     * This method returns values as you would commonly expect from a
     * wall clock or watch.
     * <p>
     * The hour from 00:00 to 00:59 will return 12.<br />
     * The hour from 01:00 to 01:59 will return 1.<br />
     * The hour from 11:00 to 11:59 will return 11.<br />
     *
     * @return true is the time is in the morning
     */
    public int getClockHourOfAmPm() {
        return ((hourOfDay + 11) % 12) + 1;
    }

    /**
     * Gets the clock hour of day, from 1 to 24.
     * <p>
     * This method returns the same as {@link #hourOfDay}, unless the
     * hour is 0, when this method returns 24.
     * <p>
     * The hour from 00:00 to 00:59 will return 24.<br />
     * The hour from 01:00 to 01:59 will return 1.<br />
     * The hour from 12:00 to 12:59 will return 12.<br />
     * The hour from 23:00 to 23:59 will return 23.<br />
     *
     * @return true is the time is in the morning
     */
    public int getClockHourOfDay() {
        return (hourOfDay == 0 ? 24 : hourOfDay);
    }

    //-----------------------------------------------------------------------
    /**
     * Implementation of the rules for the hour of day field.
     */
    private static class Rule extends TimeFieldRule {

        /** Constructor. */
        protected Rule() {
            super("HourOfDay", null, null, 0, 23);
        }
    }

}
