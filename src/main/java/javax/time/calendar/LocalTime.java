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
import javax.time.period.Periods;

/**
 * A calendrical representation of a time without a time zone, such as 10:15:30.
 * <p>
 * LocalTime is an immutable calendrical that represents a time, often
 * viewed as hour-minute-second.
 * <p>
 * This class stores all time fields, to a precision of nanoseconds.
 * It does not store or represent a date or time zone. Thus, for example, the
 * value "13:45.30.123456789" can be stored in a LocalTime.
 * <p>
 * LocalTime is thread-safe and immutable.
 *
 * @author Stephen Colebourne
 */
public final class LocalTime
        implements ReadableTime, Calendrical, Comparable<LocalTime>, Serializable {

    /**
     * A serialization identifier for this class.
     */
    private static final long serialVersionUID = 798759096L;

    /**
     * The hour, from 0 to 23.
     */
    private final int hour;
    /**
     * The minute, from 0 to 59.
     */
    private final int minute;
    /**
     * The second, from 0 to 59.
     */
    private final int second;
    /**
     * The nanosecond, from 0 to 999,999,999.
     */
    private final int nano;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>LocalTime</code>.
     * <p>
     * The second and nanosecond fields will be set to zero by this factory method.
     *
     * @param hourOfDay  the hour of day to represent, from 0 to 23
     * @param minuteOfHour  the minute of hour to represent, from 0 to 59
     * @return a LocalTime object, never null
     * @throws IllegalCalendarFieldValueException if any field is invalid
     */
    public static LocalTime time(int hourOfDay, int minuteOfHour) {
        return time(hourOfDay, minuteOfHour, 0, 0);
    }

    /**
     * Obtains an instance of <code>LocalTime</code>.
     * <p>
     * The second field will be set to zero by this factory method.
     *
     * @param hourOfDay  the hour of day to represent, from 0 to 23
     * @param minuteOfHour  the minute of hour to represent, from 0 to 59
     * @param secondOfMinute  the second of minute to represent, from 0 to 59
     * @return a LocalTime object, never null
     * @throws IllegalCalendarFieldValueException if any field is invalid
     */
    public static LocalTime time(int hourOfDay, int minuteOfHour, int secondOfMinute) {
        return time(hourOfDay, minuteOfHour, secondOfMinute, 0);
    }

//    /**
//     * Obtains an instance of <code>LocalTime</code>.
//     *
//     * @param hourOfDay  the hour of day to represent, from 0 to 23
//     * @param minuteOfHour  the minute of hour to represent, from 0 to 59
//     * @param secondOfMinute  the second of minute to represent, from 0 to 59.999,999,999
//     * @return a LocalTime object, never null
//     * @throws IllegalCalendarFieldValueException if any field is invalid
//     */
//    public static LocalTime time(int hourOfDay, int minuteOfHour, double secondOfMinute) {
//        // TODO: check maths and overflow
//        long nanos = Math.round(secondOfMinute * 1000000000);
//        long sec = nanos / 1000000000;
//        int nos = (int) (nanos % 1000000000);
//        if (nos < 0) {
//           nos += 1000000000;
//           sec--;
//        }
//        return time(hourOfDay, minuteOfHour, (int) sec, nos);
//    }

    /**
     * Obtains an instance of <code>LocalTime</code>.
     *
     * @param hourOfDay  the hour of day to represent, from 0 to 23
     * @param minuteOfHour  the minute of hour to represent, from 0 to 59
     * @param secondOfMinute  the second of minute to represent, from 0 to 59
     * @param nanoOfSecond  the nano of second to represent, from 0 to 999,999,999
     * @return a LocalTime object, never null
     * @throws IllegalCalendarFieldValueException if any field is invalid
     */
    public static LocalTime time(int hourOfDay, int minuteOfHour, int secondOfMinute, int nanoOfSecond) {
        ISOChronology.INSTANCE.hourOfDayRule().checkValue(hourOfDay);
        ISOChronology.INSTANCE.minuteOfHourRule().checkValue(minuteOfHour);
        ISOChronology.INSTANCE.secondOfMinuteRule().checkValue(secondOfMinute);
        ISOChronology.INSTANCE.nanoOfSecondRule().checkValue(nanoOfSecond);
        return new LocalTime(hourOfDay, minuteOfHour, secondOfMinute, nanoOfSecond);
    }

    /**
     * Obtains an instance of <code>LocalTime</code> from a set of calendricals.
     * <p>
     * This can be used to pass in any combination of calendricals that fully specify
     * a time. For example, HourOfDay + MinuteOfHour.
     *
     * @param calendricals  a set of calendricals that fully represent a calendar day
     * @return a LocalTime object, never null
     */
    public static LocalTime time(Calendrical... calendricals) {
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor.
     *
     * @param hourOfDay  the hour of day to represent, from 0 to 23
     * @param minuteOfHour  the minute of hour to represent, from 0 to 59
     * @param secondOfMinute  the second of minute to represent, from 0 to 59
     * @param nanoOfSecond  the nano of second to represent, from 0 to 999,999,999
     */
    private LocalTime(int hourOfDay, int minuteOfHour, int secondOfMinute, int nanoOfSecond) {
        this.hour = hourOfDay;
        this.minute = minuteOfHour;
        this.second = secondOfMinute;
        this.nano = nanoOfSecond;
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
     * This method queries whether this <code>LocalTime</code> can
     * be queried using the specified calendar field.
     *
     * @param field  the field to query, not null
     * @return true if the field is supported
     */
    public boolean isSupported(TimeFieldRule field) {
        return field.isSupported(Periods.NANOS, Periods.DAYS);
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
        if (!isSupported(field)) {
            throw new UnsupportedCalendarFieldException(field, "time");
        }
        if (field == ISOChronology.INSTANCE.hourOfDayRule()) {
            return hour;
        }
        if (field == ISOChronology.INSTANCE.minuteOfHourRule()) {
            return minute;
        }
        if (field == ISOChronology.INSTANCE.secondOfMinuteRule()) {
            return second;
        }
        if (field == ISOChronology.INSTANCE.nanoOfSecondRule()) {
            return nano;
        }
        return field.getValue(getCalendricalState());
    }

    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
    /**
     * Gets the hour of day value.
     *
     * @return the hour of day, from 0 to 23
     */
    public int getHourOfDay() {
        return hour;
    }

    /**
     * Gets the minute of hour value.
     *
     * @return the minute of hour, from 0 to 59
     */
    public int getMinuteOfHour() {
        return minute;
    }

    /**
     * Gets the second of minute value.
     *
     * @return the second of minute, from 0 to 59
     */
    public int getSecondOfMinute() {
        return second;
    }

    /**
     * Gets the nanosecond fraction of a second expressed as an int.
     *
     * @return the nano of second, from 0 to 999,999,999
     */
    public int getNanoOfSecond() {
        return nano;
    }

    /**
     * Gets the nanosecond fraction of a second expressed as a double.
     *
     * @return the nano of second, from 0 to 0.999,999,999
     */
    public double getNanoFraction() {
        return ((double) nano) / 1000000000d;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this LocalTime with the specified values altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param calendrical  the calendrical values to update to, not null
     * @return a new updated LocalTime, never null
     */
    public LocalTime with(Calendrical calendrical) {
        return null;
    }

    /**
     * Returns a copy of this LocalTime with the specified values altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param calendricals  the calendrical values to update to, no nulls
     * @return a new updated LocalTime, never null
     */
    public LocalTime with(Calendrical... calendricals) {
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this LocalTime with the hour of day value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hourOfDay  the hour of day to represent, from 0 to 23
     * @return a new updated LocalTime, never null
     */
    public LocalTime withHourOfDay(int hourOfDay) {
        return null;
    }

    /**
     * Returns a copy of this LocalTime with the minute of hour value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param minuteOfHour  the minute of hour to represent, from 0 to 59
     * @return a new updated LocalTime, never null
     */
    public LocalTime withMinuteOfHour(int minuteOfHour) {
        return null;
    }

    /**
     * Returns a copy of this LocalTime with the second of minute value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param secondOfMinute  the second of minute to represent, from 0 to 59
     * @return a new updated LocalTime, never null
     */
    public LocalTime withSecondOfMinute(int secondOfMinute) {
        return null;
    }

    /**
     * Returns a copy of this LocalTime with the nano of second value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param nanoOfSecond  the nano of second to represent, from 0 to 999,999,999
     * @return a new updated LocalTime, never null
     */
    public LocalTime withNanoOfSecond(int nanoOfSecond) {
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this LocalTime with the specified period added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param period  the period to add, not null
     * @return a new updated LocalTime, never null
     */
    public LocalTime plus(PeriodView period) {
        // TODO
        return null;
    }

    /**
     * Returns a copy of this LocalTime with the specified periods added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param periods  the periods to add, no nulls
     * @return a new updated LocalTime, never null
     */
    public LocalTime plus(PeriodView... periods) {
        // TODO
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this LocalTime with the specified period in hours added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hours  the hours to add, may be negative
     * @return a new updated LocalTime, never null
     */
    public LocalTime plusHours(int hours) {
        return null;
    }

    /**
     * Returns a copy of this LocalTime with the specified period in minutes added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param minutes  the minutes to add, may be negative
     * @return a new updated LocalTime, never null
     */
    public LocalTime plusMinutes(int minutes) {
        return null;
    }

    /**
     * Returns a copy of this LocalTime with the specified period in seconds added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param seconds  the seconds to add, may be negative
     * @return a new updated LocalTime, never null
     */
    public LocalTime plusSeconds(int seconds) {
        return null;
    }

    /**
     * Returns a copy of this LocalTime with the specified period in nanoseconds added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param nanos  the nanos to add, may be negative
     * @return a new updated LocalTime, never null
     */
    public LocalTime plusNanos(int nanos) {
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Converts this time to a <code>LocalTime</code>, trivially
     * returning <code>this</code>.
     *
     * @return <code>this</code>, never null
     */
    public LocalTime toLocalTime() {
        return this;
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this time to another time.
     *
     * @param other  the other time to compare to, not null
     * @return the comparator value, negative if less, postive if greater
     * @throws NullPointerException if <code>other</code> is null
     */
    public int compareTo(LocalTime other) {
        int cmp = MathUtils.safeCompare(hour, other.hour);
        if (cmp == 0) {
            cmp = MathUtils.safeCompare(minute, other.minute);
            if (cmp == 0) {
                cmp = MathUtils.safeCompare(second, other.second);
                if (cmp == 0) {
                    cmp = MathUtils.safeCompare(nano, other.nano);
                }
            }
        }
        return cmp;
    }

    /**
     * Is this time after the specified time.
     *
     * @param other  the other time to compare to, not null
     * @return true if this is after the specified time
     * @throws NullPointerException if <code>other</code> is null
     */
    public boolean isAfter(LocalTime other) {
        return compareTo(other) > 0;
    }

    /**
     * Is this time before the specified time.
     *
     * @param other  the other time to compare to, not null
     * @return true if this point is before the specified time
     * @throws NullPointerException if <code>other</code> is null
     */
    public boolean isBefore(LocalTime other) {
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
        if (other instanceof LocalTime) {
            LocalTime localTime = (LocalTime) other;
            return  true;
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
        return 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Outputs the time as a <code>String</code>.
     * <p>
     * The output will be one of the following formats:
     * <ul>
     * <li>'hh:mm'</li>
     * <li>'hh:mm:ss'</li>
     * <li>'hh:mm:ss.SSS'</li>
     * <li>'hh:mm:ss.SSSSSS'</li>
     * <li>'hh:mm:ss.SSSSSSSSS'</li>
     * </ul>
     * The format used will be the shortest that outputs the full value of
     * the time where the omitted parts are implied to be zero.
     *
     * @return the formatted time string, never null
     */
    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder(18);
        buf.append(hour < 10 ? "0" : "").append(hour)
            .append(minute < 10 ? ":0" : ":").append(minute);
        if (second > 0 || nano > 0) {
            buf.append(second < 10 ? "-0" : "-").append(second);
            if (nano > 0) {
                if (nano % 1000000 == 0) {
                    buf.append(Integer.toString((nano / 1000000) + 1000).substring(1));
                } else if (nano % 1000 == 0) {
                    buf.append(Integer.toString((nano / 1000) + 1000000).substring(1));
                } else {
                    buf.append(Integer.toString((nano) + 1000000000).substring(1));
                }
            }
        }
        return buf.toString();
    }

    //-----------------------------------------------------------------------
    /**
     * Adds the specified period to create a new LocalTime returning any
     * overflow in days.
     * <p>
     * This method returns an {@link Overflow} instance with the result of the
     * addition and any overflow in days.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param period  the period to add, not null
     * @return a new updated LocalTime, never null
     */
    public Overflow plusWithOverflow(PeriodView period) {
        // TODO
        LocalTime resultTime = null;
        int excessDays = 0;
        return new Overflow(resultTime, excessDays);
    }

    //-----------------------------------------------------------------------
    /**
     * Class to return the result of addition when the addition overflows
     * the capacity of a LocalTime.
     */
    public static class Overflow {
        /** The LocalTime after the addition. */
        private final LocalTime time;
        /** The overflow in days. */
        private final int days;
        /**
         * Constructor.
         *
         * @param time  the LocalTime after the addition, not null
         * @param days  the overflow in days
         */
        private Overflow(LocalTime time, int days) {
            this.time = time;
            this.days = days;
        }
        /**
         * Gets the time that was the result of the calculation.
         *
         * @return the time, never null
         */
        public LocalTime getResultTime() {
            return time;
        }
        /**
         * Gets the days overflowing from the calculation.
         *
         * @return the overflow days
         */
        public int getOverflowDays() {
            return days;
        }
        /**
         * Returns a string describing the state of this instance.
         *
         * @return the string, never null
         */
        @Override
        public String toString() {
            return getResultTime().toString() + " P" + days + "D";
        }
    }

}
