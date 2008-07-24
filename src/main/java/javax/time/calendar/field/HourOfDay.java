/*
 * Copyright (c) 2007,2008, Stephen Colebourne & Michael Nascimento Santos
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
import java.util.concurrent.atomic.AtomicReferenceArray;

import javax.time.calendar.CalendricalProvider;
import javax.time.calendar.DateTimeFieldRule;
import javax.time.calendar.Calendrical;
import javax.time.calendar.ISOChronology;
import javax.time.calendar.IllegalCalendarFieldValueException;
import javax.time.calendar.LocalTime;
import javax.time.calendar.TimeAdjustor;
import javax.time.calendar.TimeMatcher;
import javax.time.calendar.TimeProvider;

/**
 * A representation of a hour of day in the ISO-8601 calendar system.
 * <p>
 * HourOfDay is an immutable time field that can only store a hour of day.
 * It is a type-safe way of representing a hour of day in an application.
 * <p>
 * Static factory methods allow you to construct instances.
 * The hour of day may be queried using getValue().
 * <p>
 * HourOfDay is thread-safe and immutable.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
public final class HourOfDay
        implements CalendricalProvider, Comparable<HourOfDay>, Serializable, TimeAdjustor, TimeMatcher {

    /**
     * A serialization identifier for this instance.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Cache of singleton instances.
     */
    private static final AtomicReferenceArray<HourOfDay> cache = new AtomicReferenceArray<HourOfDay>(24);

    /**
     * The hour of day being represented.
     */
    private final int hourOfDay;

    //-----------------------------------------------------------------------
    /**
     * Gets the rule that defines how the hour of day field operates.
     * <p>
     * The rule provides access to the minimum and maximum values, and a
     * generic way to access values within a calendrical.
     *
     * @return the hour of day rule, never null
     */
    public static DateTimeFieldRule rule() {
        return ISOChronology.INSTANCE.hourOfDay();
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>HourOfDay</code>.
     *
     * @param hourOfDay  the hour of day to represent, from 0 to 23
     * @return the HourOfDay instance, never null
     * @throws IllegalCalendarFieldValueException if the hourOfDay is invalid
     */
    public static HourOfDay hourOfDay(int hourOfDay) {
        try {
            HourOfDay result = cache.get(hourOfDay);
            if (result == null) {
                HourOfDay temp = new HourOfDay(hourOfDay);
                cache.compareAndSet(hourOfDay, null, temp);
                result = cache.get(hourOfDay);
            }
            return result;
        } catch (IndexOutOfBoundsException ex) {
            throw new IllegalCalendarFieldValueException(
                rule(), hourOfDay, rule().getMinimumValue(), rule().getMaximumValue());
        }
    }

    /**
     * Obtains an instance of <code>HourOfDay</code> using am/pm.
     *
     * @param amPm  whether the hour is AM or PM, not null
     * @param hourOfAmPm  the hour within AM/PM, from 0 to 11
     * @return the HourOfDay instance, never null
     * @throws IllegalCalendarFieldValueException if the input is invalid
     */
    public static HourOfDay hourOfDay(AmPmOfDay amPm, int hourOfAmPm) {
        ISOChronology.INSTANCE.hourOfAmPm().checkValue(hourOfAmPm);
        int hourOfDay = amPm.getValue() * 12 + hourOfAmPm;
        return hourOfDay(hourOfDay);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>HourOfDay</code> from a time provider.
     * <p>
     * This can be used extract the hour of day directly from any implementation
     * of TimeProvider, including those in other calendar systems.
     *
     * @param timeProvider  the time provider to use, not null
     * @return the HourOfDay instance, never null
     */
    public static HourOfDay hourOfDay(TimeProvider timeProvider) {
        return timeProvider.toLocalTime().getHourOfDay();
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

    /**
     * Resolve the singleton.
     *
     * @return the singleton, never null
     */
    private Object readResolve() {
        return hourOfDay(hourOfDay);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the hour of day value.
     *
     * @return the hour of day, from 0 to 23
     */
    public int getValue() {
        return hourOfDay;
    }

    //-----------------------------------------------------------------------
    /**
     * Adjusts a time to have the the hour of day represented by this object,
     * returning a new time.
     * <p>
     * Only the hour of day field is adjusted in the result. The other time
     * fields are unaffected.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param time  the time to be adjusted, not null
     * @return the adjusted time, never null
     */
    public LocalTime adjustTime(LocalTime time) {
        if (this == time.getHourOfDay()) {
            return time;
        }
        return LocalTime.time(this, time.getMinuteOfHour(), time.getSecondOfMinute(), time.getNanoOfSecond());
    }

    /**
     * Checks if the input time has the same hour of day that is represented
     * by this object.
     *
     * @param time  the time to match, not null
     * @return true if the time matches, false otherwise
     */
    public boolean matchesTime(LocalTime time) {
        return this == time.getHourOfDay();
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
    public AmPmOfDay getAmPm() {
        return AmPmOfDay.amPmOfDay(hourOfDay / 12);
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
     * Converts this field to a <code>Calendrical</code>.
     *
     * @return the calendrical representation for this instance, never null
     */
    public Calendrical toCalendrical() {
        return Calendrical.calendrical(rule(), getValue());
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

    /**
     * A string describing the hour of day object.
     *
     * @return a string describing this object
     */
    @Override
    public String toString() {
        return "HourOfDay=" + getValue();
    }

}
