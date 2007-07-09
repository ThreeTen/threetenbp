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
public final class TimeOfDay implements RecurringMoment, Comparable<TimeOfDay> {

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
        int secondOfDay = ISOChronology.instance().toSecondOfDay(hourOfDay, minuteOfHour, 0);
        return new TimeOfDay(secondOfDay);
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
        int secondOfDay = ISOChronology.instance().toSecondOfDay(hourOfDay, minuteOfHour, secondOfMinute);
        return new TimeOfDay(secondOfDay);
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
     * Gets the value for a specific type.
     *
     * @param cls  the field type to obtain, not null
     * @return the hour of day
     */
    public int get(Class<? extends Moment> cls) {
        return 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the hour of day value.
     *
     * @return the hour of day
     */
    public int getHourOfDay() {
        return ISOChronology.instance().secondOfDayToHourOfDay(secondOfDay);
    }

    /**
     * Gets the minute of hour value.
     *
     * @return the minute of hour
     */
    public int getMinuteOfHour() {
        return ISOChronology.instance().secondOfDayToMinuteOfHour(secondOfDay);
    }

    /**
     * Gets the second of minute value.
     *
     * @return the second of minute
     */
    public int getSecondOfMinute() {
        return ISOChronology.instance().secondOfDayToSecondOfMinute(secondOfDay);
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
    public TimeOfDay with(Moment moment) {
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
    public TimeOfDay with(Moment... moments) {
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
        int secondOfDay = ISOChronology.instance().toSecondOfDay(hourOfDay, getMinuteOfHour(), getSecondOfMinute());
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
        int secondOfDay = ISOChronology.instance().toSecondOfDay(getHourOfDay(), minuteOfHour, getSecondOfMinute());
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
        int secondOfDay = ISOChronology.instance().toSecondOfDay(getHourOfDay(), getMinuteOfHour(), secondOfMinute);
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
    public TimeOfDay plus(Period period) {
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
    public TimeOfDay plus(Period... periods) {
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
        int secondOfDay = ISOChronology.instance().toSecondOfDayPlusWrapped(
                getHourOfDay(), hours, getMinuteOfHour(), 0, getSecondOfMinute(), 0);
        return new TimeOfDay(secondOfDay);
    }

//    /**
//     * Returns a copy of this TimeOfDay with the specified number of hours added.
//     * <p>
//     * This instance is immutable and unaffected by this method call.
//     *
//     * @param hours  the hours to add, not null
//     * @return a new updated TimeOfDay
//     */
//    public TimeOfDay plusHours(Hours hours) {
//        return plusHours(hours.getHours());
//    }

    /**
     * Returns a copy of this TimeOfDay with the specified number of minutes added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param minutes  the minutes to add
     * @return a new updated TimeOfDay
     */
    public TimeOfDay plusMinutes(int minutes) {
        int secondOfDay = ISOChronology.instance().toSecondOfDayPlusWrapped(
                getHourOfDay(), 0, getMinuteOfHour(), minutes, getSecondOfMinute(), 0);
        return new TimeOfDay(secondOfDay);
    }

//    /**
//     * Returns a copy of this TimeOfDay with the specified number of minutes added.
//     * <p>
//     * This instance is immutable and unaffected by this method call.
//     *
//     * @param minutes  the minutes to add, not null
//     * @return a new updated TimeOfDay
//     */
//    public TimeOfDay plusMinutes(Minutes minutes) {
//        return plusMinutes(minutes.getMinutes());
//    }

    /**
     * Returns a copy of this TimeOfDay with the specified number of seconds added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param seconds  the secondsto add
     * @return a new updated TimeOfDay
     */
    public TimeOfDay plusSeconds(int seconds) {
        int secondOfDay = ISOChronology.instance().toSecondOfDayPlusWrapped(
                getHourOfDay(), 0, getMinuteOfHour(), 0, getSecondOfMinute(), seconds);
        return new TimeOfDay(secondOfDay);
    }

//    /**
//     * Returns a copy of this TimeOfDay with the specified number of seconds added.
//     * <p>
//     * This instance is immutable and unaffected by this method call.
//     *
//     * @param seconds  the secondsto add, not null
//     * @return a new updated TimeOfDay
//     */
//    public TimeOfDay plusSeconds(Seconds seconds) {
//        return plusSeconds(seconds.getSeconds());
//    }

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
