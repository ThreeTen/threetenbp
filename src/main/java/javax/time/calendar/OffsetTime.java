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

import javax.time.period.PeriodView;

/**
 * A calendrical representation of a time with a zone offset from UTC.
 * <p>
 * OffsetTime is an immutable calendrical that represents a time, often
 * viewed as hour-minute-second-offset.
 * This class stores all time fields, to a precision of nanoseconds,
 * as well as a zone offset.
 * Thus, for example, the value "13:45.30.123456789+02:00" can be stored
 * in a OffsetTime.
 * <p>
 * OffsetTime is thread-safe and immutable.
 *
 * @author Stephen Colebourne
 */
public final class OffsetTime
        implements Calendrical, Comparable<OffsetTime>, Serializable {

    /**
     * A serialization identifier for this class.
     */
    private static final long serialVersionUID = -1751032571L;

    /**
     * The local time.
     */
    private final LocalTime time;
    /**
     * The zone offset from UTC.
     */
    private final ZoneOffset offset;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>OffsetTime</code>.
     * <p>
     * The second and nanosecond fields will be set to zero by this factory method.
     *
     * @param hourOfDay  the hour of day to represent, from 0 to 23
     * @param minuteOfHour  the minute of hour to represent, from 0 to 59
     * @param offset  the zone offset, not null
     * @return a OffsetTime object, never null
     * @throws IllegalCalendarFieldValueException if any field is invalid
     */
    public static OffsetTime time(int hourOfDay, int minuteOfHour, ZoneOffset offset) {
        return time(hourOfDay, minuteOfHour, 0, 0, offset);
    }

    /**
     * Obtains an instance of <code>OffsetTime</code>.
     * <p>
     * The second field will be set to zero by this factory method.
     *
     * @param hourOfDay  the hour of day to represent, from 0 to 23
     * @param minuteOfHour  the minute of hour to represent, from 0 to 59
     * @param secondOfMinute  the second of minute to represent, from 0 to 59
     * @param offset  the zone offset, not null
     * @return a OffsetTime object, never null
     * @throws IllegalCalendarFieldValueException if any field is invalid
     */
    public static OffsetTime time(int hourOfDay, int minuteOfHour, int secondOfMinute, ZoneOffset offset) {
        return time(hourOfDay, minuteOfHour, secondOfMinute, 0, offset);
    }

    /**
     * Obtains an instance of <code>ZonedDateTime</code>.
     *
     * @param hourOfDay  the hour of day to represent, from 0 to 23
     * @param minuteOfHour  the minute of hour to represent, from 0 to 59
     * @param secondOfMinute  the second of minute to represent, from 0 to 59
     * @param nanoOfSecond  the nano of second to represent, from 0 to 999,999,999
     * @param offset  the zone offset, not null
     * @return a OffsetTime object, never null
     * @throws IllegalCalendarFieldValueException if any field is invalid
     */
    public static OffsetTime time(int hourOfDay, int minuteOfHour, int secondOfMinute, int nanoOfSecond, ZoneOffset offset) {
        LocalTime time = LocalTime.time(hourOfDay, minuteOfHour, secondOfMinute, nanoOfSecond);
        if (offset == null) {
            throw new NullPointerException("The zone offset must not be null");
        }
        return new OffsetTime(time, offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor.
     *
     * @param time  the time, not null
     * @param offset  the zone offset, not null
     */
    private OffsetTime(LocalTime time, ZoneOffset offset) {
        this.time = time;
        this.offset = offset;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the calendrical state which provides internal access to
     * this time.
     *
     * @return the calendar state for this instance, never null
     */
    @Override
    public CalendricalState getCalendricalState() {
        return null;  // TODO
    }

    /**
     * Gets the chronology that describes the calendar system rules for
     * this time.
     *
     * @return the ISO chronology, never null
     */
    public ISOChronology getChronology() {
        return ISOChronology.INSTANCE;
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if the specified calendar field is supported.
     * <p>
     * This method queries whether this <code>OffsetTime</code> can
     * be queried using the specified calendar field.
     *
     * @param field  the field to query, not null
     * @return true if the field is supported
     */
    public boolean isSupported(TimeFieldRule field) {
        return time.isSupported(field);
    }

    /**
     * Gets the value of the specified calendar field.
     * <p>
     * This method queries the value of the specified calendar field.
     * If the calendar field is not supported then an exception is thrown.
     *
     * @param field  the field to query, not null
     * @return the value for the field
     * @throws UnsupportedCalendarFieldException if the field is not supported
     */
    public int get(TimeFieldRule field) {
        return time.get(field);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets an instance of <code>LocalTime</code> which represents the
     * time of this object but without the zone offset.
     *
     * @return the time object, never null
     */
    public LocalTime localTime() {
        return time;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the zone offset representing how far ahead or behind UTC the time is.
     *
     * @return the zone offset, never null
     */
    public ZoneOffset getOffset() {
        return offset;
    }

    /**
     * Returns a copy of this OffsetTime with a different zone offset.
     * <p>
     * This method changes the zoned time to a different offset.
     * No calculation is performed - the result simply represents the same
     * time and the new offset.
     * <p>
     * To take into account the offsets and adjust the time fields,
     * use {@link #adjustLocalTime(ZoneOffset)}.
     *
     * @param offset  the zone offset to change to, not null
     * @return a new updated OffsetTime, never null
     */
    public OffsetTime withOffset(ZoneOffset offset) {
        if (offset == null) {
            throw new NullPointerException("Zone offset must not be null");
        }
        return offset == this.offset ? this : new OffsetTime(time, offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Adjusts the local time using the specified offset.
     * <p>
     * This method changes the zoned time from one offset to another.
     * If this zoned date represents 10:30+02:00 and the offset specified is
     * +03:00, then this method will return 11:30+03:00.
     * <p>
     * To change the offset whilst keeping the local time,
     * use {@link #withOffset(ZoneOffset)}.
     *
     * @param offset  the zone offset to change to, not null
     * @return a new updated OffsetTime, never null
     */
    public OffsetTime adjustLocalTime(ZoneOffset offset) {
        if (offset.equals(this.offset)) {
            return this;
        }
        int difference = offset.getAmountSeconds() - this.offset.getAmountSeconds();
        LocalTime adjusted = time.plusSeconds(difference);
        return new OffsetTime(adjusted, offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the hour of day value.
     *
     * @return the hour of day, from 0 to 23
     */
    public int getHourOfDay() {
        return time.getHourOfDay();
    }

    /**
     * Gets the minute of hour value.
     *
     * @return the minute of hour, from 0 to 59
     */
    public int getMinuteOfHour() {
        return time.getMinuteOfHour();
    }

    /**
     * Gets the second of minute value.
     *
     * @return the second of minute, from 0 to 59
     */
    public int getSecondOfMinute() {
        return time.getSecondOfMinute();
    }

    /**
     * Gets the nanosecond fraction of a second expressed as an int.
     *
     * @return the nano of second, from 0 to 999,999,999
     */
    public int getNanoOfSecond() {
        return time.getNanoOfSecond();
    }

    /**
     * Gets the nanosecond fraction of a second expressed as a double.
     *
     * @return the nano of second, from 0 to 0.999,999,999
     */
    public double getNanoFraction() {
        return time.getNanoFraction();
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this OffsetTime with the specified values altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param calendrical  the calendrical values to update to, not null
     * @return a new updated OffsetTime, never null
     */
    public OffsetTime with(Calendrical calendrical) {
        LocalTime newTime = time.with(calendrical);
        return newTime == this.time ? this : new OffsetTime(newTime, offset);
    }

    /**
     * Returns a copy of this OffsetTime with the specified values altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param calendricals  the calendrical values to update to, no nulls
     * @return a new updated OffsetTime, never null
     */
    public OffsetTime with(Calendrical... calendricals) {
        LocalTime newTime = time.with(calendricals);
        return newTime == this.time ? this : new OffsetTime(newTime, offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this OffsetTime with the hour of day value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hourOfDay  the hour of day to represent, from 0 to 23
     * @return a new updated OffsetTime, never null
     */
    public OffsetTime withHourOfDay(int hourOfDay) {
        LocalTime newTime = time.withHourOfDay(hourOfDay);
        return newTime == this.time ? this : new OffsetTime(newTime, offset);
    }

    /**
     * Returns a copy of this OffsetTime with the minute of hour value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param minuteOfHour  the minute of hour to represent, from 0 to 59
     * @return a new updated OffsetTime, never null
     */
    public OffsetTime withMinuteOfHour(int minuteOfHour) {
        LocalTime newTime = time.withMinuteOfHour(minuteOfHour);
        return newTime == this.time ? this : new OffsetTime(newTime, offset);
    }

    /**
     * Returns a copy of this OffsetTime with the second of minute value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param secondOfMinute  the second of minute to represent, from 0 to 59
     * @return a new updated OffsetTime, never null
     */
    public OffsetTime withSecondOfMinute(int secondOfMinute) {
        LocalTime newTime = time.withSecondOfMinute(secondOfMinute);
        return newTime == this.time ? this : new OffsetTime(newTime, offset);
    }

    /**
     * Returns a copy of this OffsetTime with the nano of second value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param nanoOfSecond  the nano of second to represent, from 0 to 999,999,999
     * @return a new updated OffsetTime, never null
     */
    public OffsetTime withNanoOfSecond(int nanoOfSecond) {
        LocalTime newTime = time.withNanoOfSecond(nanoOfSecond);
        return newTime == this.time ? this : new OffsetTime(newTime, offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this OffsetTime with the specified period added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param period  the period to add, not null
     * @return a new updated OffsetTime, never null
     */
    public OffsetTime plus(PeriodView period) {
        LocalTime newTime = time.plus(period);
        return newTime == this.time ? this : new OffsetTime(newTime, offset);
    }

    /**
     * Returns a copy of this OffsetTime with the specified periods added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param periods  the periods to add, no nulls
     * @return a new updated OffsetTime, never null
     */
    public OffsetTime plus(PeriodView... periods) {
        LocalTime newTime = time.plus(periods);
        return newTime == this.time ? this : new OffsetTime(newTime, offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this OffsetTime with the specified period in hours added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hours  the hours to add, may be negative
     * @return a new updated OffsetTime, never null
     */
    public OffsetTime plusHours(int hours) {
        LocalTime newTime = time.plusHours(hours);
        return newTime == this.time ? this : new OffsetTime(newTime, offset);
    }

    /**
     * Returns a copy of this OffsetTime with the specified period in minutes added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param minutes  the minutes to add, may be negative
     * @return a new updated OffsetTime, never null
     */
    public OffsetTime plusMinutes(int minutes) {
        LocalTime newTime = time.plusMinutes(minutes);
        return newTime == this.time ? this : new OffsetTime(newTime, offset);
    }

    /**
     * Returns a copy of this OffsetTime with the specified period in seconds added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param seconds  the seconds to add, may be negative
     * @return a new updated OffsetTime, never null
     */
    public OffsetTime plusSeconds(int seconds) {
        LocalTime newTime = time.plusSeconds(seconds);
        return newTime == this.time ? this : new OffsetTime(newTime, offset);
    }

    /**
     * Returns a copy of this OffsetTime with the specified period in nanoseconds added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param nanos  the nanos to add, may be negative
     * @return a new updated OffsetTime, never null
     */
    public OffsetTime plusNanos(int nanos) {
        LocalTime newTime = time.plusNanos(nanos);
        return newTime == this.time ? this : new OffsetTime(newTime, offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this time to another time taking into account the zone offset.
     * <p>
     * This comparison normalizes both times to UTC using the zone offset.
     * It then compares the local time.
     *
     * @param other  the other time to compare to, not null
     * @return the comparator value, negative if less, postive if greater
     * @throws NullPointerException if <code>other</code> is null
     */
    public int compareTo(OffsetTime other) {
        if (offset.equals(other.offset)) {
            return time.compareTo(other.time);
        }
        LocalTime thisUTC = time.plusSeconds(-offset.getAmountSeconds());
        LocalTime otherUTC = other.time.plusSeconds(other.offset.getAmountSeconds());
        return thisUTC.compareTo(otherUTC);
    }

    /**
     * Is this time after the specified time.
     *
     * @param other  the other time to compare to, not null
     * @return true if this is after the specified time
     * @throws NullPointerException if <code>other</code> is null
     */
    public boolean isAfter(OffsetTime other) {
        return compareTo(other) > 0;
    }

    /**
     * Is this time before the specified time.
     *
     * @param other  the other time to compare to, not null
     * @return true if this point is before the specified time
     * @throws NullPointerException if <code>other</code> is null
     */
    public boolean isBefore(OffsetTime other) {
        return compareTo(other) < 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Is this time equal to the specified time.
     *
     * @param other  the other time to compare to, null returns false
     * @return true if this point is equal to the specified time
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof OffsetTime) {
            OffsetTime zonedTime = (OffsetTime) other;
            return time.equals(zonedTime.time) && offset.equals(zonedTime.offset);
        }
        return false;
    }

    /**
     * A hashcode for this time.
     *
     * @return a suitable hashcode
     */
    @Override
    public int hashCode() {
        return time.hashCode() ^ offset.hashCode();
    }

    //-----------------------------------------------------------------------
    /**
     * Outputs the time as a <code>String</code>.
     * <p>
     * The output will be one of the following formats:
     * <ul>
     * <li>'hh:mmZ'</li>
     * <li>'hh:mm:ssZ'</li>
     * <li>'hh:mm:ss.SSSZ'</li>
     * <li>'hh:mm:ss.SSSSSSZ'</li>
     * <li>'hh:mm:ss.SSSSSSSSSZ'</li>
     * </ul>
     * where 'Z' is the id of the zone offset, such as '+02:30' or 'Z'.
     * The format used will be the shortest that outputs the full value of
     * the time where the omitted parts are implied to be zero.
     *
     * @return the formatted time string, never null
     */
    @Override
    public String toString() {
        return time.toString() + offset.toString();
    }

}
