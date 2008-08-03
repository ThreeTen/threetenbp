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
package javax.time.calendar;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.time.MathUtils;
import javax.time.calendar.field.HourOfDay;
import javax.time.calendar.field.MinuteOfHour;
import javax.time.calendar.field.NanoOfSecond;
import javax.time.calendar.field.SecondOfMinute;
import javax.time.period.Period;
import javax.time.period.PeriodProvider;

/**
 * A time without time zone in the ISO-8601 calendar system,
 * such as '10:15:30'.
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
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
public final class LocalTime
        implements TimeProvider, CalendricalProvider, Comparable<LocalTime>, Serializable, TimeMatcher, TimeAdjuster {

    /**
     * Constant for the local time of midnight, 00:00.
     */
    public static final LocalTime MIDNIGHT = new LocalTime(
            HourOfDay.hourOfDay(0), MinuteOfHour.minuteOfHour(0),
            SecondOfMinute.secondOfMinute(0), NanoOfSecond.ZERO);
    /**
     * Constant for the local time of midday, 12:00.
     */
    public static final LocalTime MIDDAY = new LocalTime(
            HourOfDay.hourOfDay(12), MinuteOfHour.minuteOfHour(0),
            SecondOfMinute.secondOfMinute(0), NanoOfSecond.ZERO);
    /**
     * A serialization identifier for this class.
     */
    private static final long serialVersionUID = 798759096L;
    /** Hours per minute. */
    private static final int HOURS_PER_DAY = 24;
    /** Minutes per hour. */
    private static final int MINUTES_PER_HOUR = 60;
    /** Minutes per day. */
    private static final int MINUTES_PER_DAY = MINUTES_PER_HOUR * HOURS_PER_DAY;
    /** Seconds per minute. */
    private static final int SECONDS_PER_MINUTE = 60;
    /** Seconds per hour. */
    private static final int SECONDS_PER_HOUR = SECONDS_PER_MINUTE * MINUTES_PER_HOUR;
    /** Seconds per day. */
    private static final int SECONDS_PER_DAY = SECONDS_PER_HOUR * HOURS_PER_DAY;
    /** Nanos per second. */
    private static final long NANOS_PER_SECOND = 1000000000L;
    /** Nanos per minute. */
    private static final long NANOS_PER_MINUTE = NANOS_PER_SECOND * SECONDS_PER_MINUTE;
    /** Nanos per hour. */
    private static final long NANOS_PER_HOUR = NANOS_PER_MINUTE * MINUTES_PER_HOUR;
    /** Nanos per day. */
    private static final long NANOS_PER_DAY = NANOS_PER_HOUR * HOURS_PER_DAY;

    /**
     * The hour, never null.
     */
    private final HourOfDay hour;
    /**
     * The minute, never null.
     */
    private final MinuteOfHour minute;
    /**
     * The second, never null.
     */
    private final SecondOfMinute second;
    /**
     * The nanosecond, never null.
     */
    private final NanoOfSecond nano;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>LocalTime</code> from an hour and minute,
     * setting the second and nanosecond to zero.
     *
     * @param hourOfDay  the hour of day to represent, not null
     * @param minuteOfHour  the minute of hour to represent, not null
     * @return a LocalTime object, never null
     */
    public static LocalTime time(HourOfDay hourOfDay, MinuteOfHour minuteOfHour) {
        return time(hourOfDay, minuteOfHour, SecondOfMinute.secondOfMinute(0), NanoOfSecond.ZERO);
    }

    /**
     * Obtains an instance of <code>LocalTime</code> from an hour, minute and
     * second, setting the nanosecond to zero.
     *
     * @param hourOfDay  the hour of day to represent, not null
     * @param minuteOfHour  the minute of hour to represent, not null
     * @param secondOfMinute  the second of minute to represent, not null
     * @return a LocalTime object, never null
     */
    public static LocalTime time(
            HourOfDay hourOfDay, MinuteOfHour minuteOfHour, SecondOfMinute secondOfMinute) {
        return time(hourOfDay, minuteOfHour, secondOfMinute, NanoOfSecond.ZERO);
    }

    /**
     * Obtains an instance of <code>LocalTime</code> from an hour, minute,
     * second and nanosecond.
     *
     * @param hourOfDay  the hour of day to represent, not null
     * @param minuteOfHour  the minute of hour to represent, not null
     * @param secondOfMinute  the second of minute to represent, not null
     * @param nanoOfSecond  the nano of second to represent, not null
     * @return a LocalTime object, never null
     */
    public static LocalTime time(
            HourOfDay hourOfDay, MinuteOfHour minuteOfHour,
            SecondOfMinute secondOfMinute, NanoOfSecond nanoOfSecond) {
        if (hourOfDay == null) {
            throw new NullPointerException("HourOfDay must not be null");
        }
        if (minuteOfHour == null) {
            throw new NullPointerException("MinuteOfHour must not be null");
        }
        if (secondOfMinute == null) {
            throw new NullPointerException("SecondOfMinute must not be null");
        }
        if (nanoOfSecond == null) {
            throw new NullPointerException("NanoOfSecond must not be null");
        }
        if (hourOfDay.getValue() == 0 && minuteOfHour.getValue() == 0 &&
                secondOfMinute.getValue() == 0 && nanoOfSecond == NanoOfSecond.ZERO) {
            return MIDNIGHT;
        }
        if (hourOfDay.getValue() == 12 && minuteOfHour.getValue() == 0 &&
                secondOfMinute.getValue() == 0 && nanoOfSecond == NanoOfSecond.ZERO) {
            return MIDDAY;
        }
        return new LocalTime(hourOfDay, minuteOfHour, secondOfMinute, nanoOfSecond);
    }

    /**
     * Obtains an instance of <code>LocalTime</code>.
     * <p>
     * The second and nanosecond fields will be set to zero by this factory method.
     *
     * @param hourOfDay  the hour of day to represent, from 0 to 23
     * @param minuteOfHour  the minute of hour to represent, from 0 to 59
     * @return a LocalTime object, never null
     * @throws IllegalCalendarFieldValueException if the value of any field is out of range
     */
    public static LocalTime time(int hourOfDay, int minuteOfHour) {
        if (hourOfDay == 0 && minuteOfHour == 0) {
            return MIDNIGHT;
        }
        if (hourOfDay == 12 && minuteOfHour == 0) {
            return MIDDAY;
        }
        return new LocalTime(
                HourOfDay.hourOfDay(hourOfDay),
                MinuteOfHour.minuteOfHour(minuteOfHour),
                SecondOfMinute.secondOfMinute(0),
                NanoOfSecond.ZERO);
    }

    /**
     * Obtains an instance of <code>LocalTime</code>.
     * <p>
     * The nanosecond field will be set to zero by this factory method.
     *
     * @param hourOfDay  the hour of day to represent, from 0 to 23
     * @param minuteOfHour  the minute of hour to represent, from 0 to 59
     * @param secondOfMinute  the second of minute to represent, from 0 to 59
     * @return a LocalTime object, never null
     * @throws IllegalCalendarFieldValueException if the value of any field is out of range
     */
    public static LocalTime time(int hourOfDay, int minuteOfHour, int secondOfMinute) {
        if (hourOfDay == 0 && minuteOfHour == 0 && secondOfMinute == 0) {
            return MIDNIGHT;
        }
        if (hourOfDay == 12 && minuteOfHour == 0 && secondOfMinute == 0) {
            return MIDDAY;
        }
        return new LocalTime(
                HourOfDay.hourOfDay(hourOfDay),
                MinuteOfHour.minuteOfHour(minuteOfHour),
                SecondOfMinute.secondOfMinute(secondOfMinute),
                NanoOfSecond.ZERO);

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
     * @throws IllegalCalendarFieldValueException if the value of any field is out of range
     */
    public static LocalTime time(int hourOfDay, int minuteOfHour, int secondOfMinute, int nanoOfSecond) {
        if (hourOfDay == 0 && minuteOfHour == 0 && secondOfMinute == 0 && nanoOfSecond == 0) {
            return MIDNIGHT;
        }
        if (hourOfDay == 12 && minuteOfHour == 0 && secondOfMinute == 0 && nanoOfSecond == 0) {
            return MIDDAY;
        }
        return new LocalTime(
                HourOfDay.hourOfDay(hourOfDay),
                MinuteOfHour.minuteOfHour(minuteOfHour),
                SecondOfMinute.secondOfMinute(secondOfMinute),
                NanoOfSecond.nanoOfSecond(nanoOfSecond));
    }

    /**
     * Obtains an instance of <code>LocalTime</code> from a number of nanos of day.
     *
     * @param nanoOfDay  the nano of day, from <code>0</code> to <code>24 * 60 * 60 * 1,000,000,000 - 1</code>
     * @return a LocalTime object, never null
     * @throws CalendarConversionException if the nanos of day value is invalid
     */
    public static LocalTime fromNanoOfDay(long nanoOfDay) {
        if (nanoOfDay < 0) {
            throw new CalendarConversionException("Cannot create LocalTime from nanos of day as value " +
                    nanoOfDay + " must not be negative");
        }
        if (nanoOfDay >= NANOS_PER_DAY) {
            throw new CalendarConversionException("Cannot create LocalTime from nanos of day as value " +
                    nanoOfDay + " must be less than " + NANOS_PER_DAY);
        }
        
        int hours = (int) (nanoOfDay / NANOS_PER_HOUR);
        nanoOfDay -= hours * NANOS_PER_HOUR;
        int minutes = (int) (nanoOfDay / NANOS_PER_MINUTE);
        nanoOfDay -= minutes * NANOS_PER_MINUTE;
        int seconds = (int) (nanoOfDay / NANOS_PER_SECOND);
        nanoOfDay -= seconds * NANOS_PER_SECOND;
        return time(hours, minutes, seconds, (int) nanoOfDay);
    }

    /**
     * Obtains an instance of <code>LocalTime</code> from a time provider.
     *
     * @param timeProvider  the time provider to use, not null
     * @return a LocalTime object, never null
     */
    public static LocalTime time(TimeProvider timeProvider) {
        if (timeProvider == null) {
            throw new NullPointerException("TimeProvider must not be null");
        }
        LocalTime result = timeProvider.toLocalTime();
        if (result == null) {
            throw new NullPointerException("The implementation of TimeProvider must not return null");
        }
        return result;
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor, previously validated.
     *
     * @param hourOfDay  the hour of day to represent, not null
     * @param minuteOfHour  the minute of hour to represent, not null
     * @param secondOfMinute  the second of minute to represent, not null
     * @param nanoOfSecond  the nano of second to represent, not null
     */
    private LocalTime(
            HourOfDay hourOfDay, MinuteOfHour minuteOfHour,
            SecondOfMinute secondOfMinute, NanoOfSecond nanoOfSecond) {
        this.hour = hourOfDay;
        this.minute = minuteOfHour;
        this.second = secondOfMinute;
        this.nano = nanoOfSecond;
    }

    //-----------------------------------------------------------------------
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
     * @param fieldRule  the field to query, null returns false
     * @return true if the field is supported, false otherwise
     */
    public boolean isSupported(DateTimeFieldRule fieldRule) {
        return fieldRule != null && fieldRule.getValueQuiet(null, this) != null;
    }

    /**
     * Gets the value of the specified calendar field.
     * <p>
     * This method queries the value of the specified calendar field.
     * If the calendar field is not supported then an exception is thrown.
     *
     * @param fieldRule  the field to query, not null
     * @return the value for the field
     * @throws UnsupportedCalendarFieldException if no value for the field is found
     */
    public int get(DateTimeFieldRule fieldRule) {
        return toCalendrical().getValue(fieldRule);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the hour of day field.
     * <p>
     * This method provides access to an object representing the hour of day field.
     * This can be used to access the {@link HourOfDay#getValue() int value}.
     *
     * @return the hour of day, never null
     */
    public HourOfDay getHourOfDay() {
        return hour;
    }

    /**
     * Gets the minute of hour field.
     * <p>
     * This method provides access to an object representing the minute of hour field.
     * This can be used to access the {@link MinuteOfHour#getValue() int value}.
     *
     * @return the minute of hour, never null
     */
    public MinuteOfHour getMinuteOfHour() {
        return minute;
    }

    /**
     * Gets the second of minute field.
     * <p>
     * This method provides access to an object representing the second of minute field.
     * This can be used to access the {@link SecondOfMinute#getValue() int value}.
     *
     * @return the second of minute, never null
     */
    public SecondOfMinute getSecondOfMinute() {
        return second;
    }

    /**
     * Gets the nano of second field.
     * <p>
     * This method provides access to an object representing the nano of second field.
     * This can be used to access the {@link NanoOfSecond#getValue() int value}.
     *
     * @return the nano of second, never null
     */
    public NanoOfSecond getNanoOfSecond() {
        return nano;
    }

//    /**
//     * Gets the second and nanosecond, expressed as a double in seconds.
//     *
//     * @return the nano of second, from 0 to 59.999,999,999
//     */
//    public double getFractionalSecondOfMinute() {
//        // TODO: check maths and write tests
//        return (((double) nano.getValue()) / 1000000000d) + second.getValue();
//    }
//
//    /**
//     * Gets the time as a fraction of a day, expressed as a double in days.
//     *
//     * @return the nano of second, from 0 to &lt; 1
//     */
//    public double getFractionalDay() {
//        // TODO: check maths and write tests
//        return (((double) toNanoOfDay()) / ((double) NANOS_PER_DAY));
//    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this LocalTime with the time altered using the adjuster.
     * <p>
     * Adjusters can be used to alter the time in various ways.
     * A simple adjuster might simply set the one of the fields, such as the hour field.
     * A more complex adjuster might set the time to end of the working day.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param adjuster  the adjuster to use, not null
     * @return a new updated LocalTime, never null
     */
    public LocalTime with(TimeAdjuster adjuster) {
        LocalTime time = adjuster.adjustTime(this);
        if (time == null) {
            throw new NullPointerException("The implementation of TimeAdjuster must not return null");
        }
        return time;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this LocalTime with the hour of day value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hourOfDay  the hour of day to represent, from 0 to 23
     * @return a new updated LocalTime, never null
     * @throws IllegalCalendarFieldValueException if the hour value is invalid
     */
    public LocalTime withHourOfDay(int hourOfDay) {
        if (hourOfDay == getHourOfDay().getValue()) {
            return this;
        }
        HourOfDay newHour = HourOfDay.hourOfDay(hourOfDay);
        return time(newHour, minute, second, nano);
    }

    /**
     * Returns a copy of this LocalTime with the minute of hour value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param minuteOfHour  the minute of hour to represent, from 0 to 59
     * @return a new updated LocalTime, never null
     * @throws IllegalCalendarFieldValueException if the minute value is invalid
     */
    public LocalTime withMinuteOfHour(int minuteOfHour) {
        if (minuteOfHour == getMinuteOfHour().getValue()) {
            return this;
        }
        MinuteOfHour newMinute = MinuteOfHour.minuteOfHour(minuteOfHour);
        return time(hour, newMinute, second, nano);
    }

    /**
     * Returns a copy of this LocalTime with the second of minute value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param secondOfMinute  the second of minute to represent, from 0 to 59
     * @return a new updated LocalTime, never null
     * @throws IllegalCalendarFieldValueException if the second value is invalid
     */
    public LocalTime withSecondOfMinute(int secondOfMinute) {
        if (secondOfMinute == getSecondOfMinute().getValue()) {
            return this;
        }
        SecondOfMinute newSecond = SecondOfMinute.secondOfMinute(secondOfMinute);
        return time(hour, minute, newSecond, nano);
    }

    /**
     * Returns a copy of this LocalTime with the nano of second value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param nanoOfSecond  the nano of second to represent, from 0 to 999,999,999
     * @return a new updated LocalTime, never null
     * @throws IllegalCalendarFieldValueException if the nanos value is invalid
     */
    public LocalTime withNanoOfSecond(int nanoOfSecond) {
        if (nanoOfSecond == getNanoOfSecond().getValue()) {
            return this;
        }
        NanoOfSecond newNano = NanoOfSecond.nanoOfSecond(nanoOfSecond);
        return time(hour, minute, second, newNano);
    }

    //-----------------------------------------------------------------------
    //TODO: consider delegating plus* methods to plus*WithOverflow versions
    /**
     * Returns a copy of this LocalTime with the specified period added.
     * <p>
     * This adds the amount in hours, minutes and seconds from the specified period to this time.
     * Any date amounts, such as years, months or days are ignored.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param periodProvider  the period to add, not null
     * @return a new updated LocalTime, never null
     */
    public LocalTime plus(PeriodProvider periodProvider) {
        Period period = Period.period(periodProvider);
        // safe from overflow
        long totalNanos = period.getHours() * NANOS_PER_HOUR +
                period.getMinutes() * NANOS_PER_MINUTE +
                period.getSeconds() * NANOS_PER_SECOND;
        return plusNanos(totalNanos).plusNanos(period.getNanos());
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this LocalTime with the specified period in hours added.
     * <p>
     * If the resulting hour is lesser than 0 or greater than 23, the field <b>rolls</b>.
     * For instance, 24 becomes 0 and -1 becomes 23.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hours  the hours to add, may be negative
     * @return a new updated LocalTime, never null
     */
    public LocalTime plusHours(int hours) {
        if (hours == 0) {
            return this;
        }
        int newHour = ((hours % HOURS_PER_DAY) + hour.getValue() + HOURS_PER_DAY) % HOURS_PER_DAY;
        return withHourOfDay(newHour);
    }

    /**
     * Returns a copy of this LocalTime with the specified period in minutes added.
     * <p>
     * If the resulting hour is lesser than 0 or greater than 23, the hour field <b>rolls</b>.
     * For instance, 24 becomes 0 and -1 becomes 23.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param minutes  the minutes to add, may be negative
     * @return a new updated LocalTime, never null
     */
    public LocalTime plusMinutes(int minutes) {
        if (minutes == 0) {
            return this;
        }
        int mofd = hour.getValue() * MINUTES_PER_HOUR + minute.getValue();
        int newMofd = ((minutes % MINUTES_PER_DAY) + mofd + MINUTES_PER_DAY) % MINUTES_PER_DAY;
        if (mofd == newMofd) {
            return this;
        }
        HourOfDay newHour = HourOfDay.hourOfDay(newMofd / MINUTES_PER_HOUR);
        MinuteOfHour newMinute = MinuteOfHour.minuteOfHour(newMofd % MINUTES_PER_HOUR);
        return time(newHour, newMinute, second, nano);
    }

    /**
     * Returns a copy of this LocalTime with the specified period in seconds added.
     * <p>
     * If the resulting hour is lesser than 0 or greater than 23, the hour field <b>rolls</b>.
     * For instance, 24 becomes 0 and -1 becomes 23.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param seconds  the seconds to add, may be negative
     * @return a new updated LocalTime, never null
     */
    public LocalTime plusSeconds(int seconds) {
        if (seconds == 0) {
            return this;
        }
        int sofd = hour.getValue() * SECONDS_PER_HOUR +
                    minute.getValue() * SECONDS_PER_MINUTE + second.getValue();
        int newSofd = ((seconds % SECONDS_PER_DAY) + sofd + SECONDS_PER_DAY) % SECONDS_PER_DAY;
        if (sofd == newSofd) {
            return this;
        }
        HourOfDay newHour = HourOfDay.hourOfDay(newSofd / SECONDS_PER_HOUR);
        MinuteOfHour newMinute = MinuteOfHour.minuteOfHour((newSofd / SECONDS_PER_MINUTE) % MINUTES_PER_HOUR);
        SecondOfMinute newSecond = SecondOfMinute.secondOfMinute(newSofd % SECONDS_PER_MINUTE);
        return time(newHour, newMinute, newSecond, nano);
    }

    /**
     * Returns a copy of this LocalTime with the specified period in nanoseconds added.
     * <p>
     * If the resulting hour is lesser than 0 or greater than 23, the hour field <b>rolls</b>.
     * For instance, 24 becomes 0 and -1 becomes 23.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param nanos  the nanos to add, may be negative
     * @return a new updated LocalTime, never null
     */
    public LocalTime plusNanos(long nanos) {
        if (nanos == 0) {
            return this;
        }
        long nofd = toNanoOfDay();
        long newNofd = ((nanos % NANOS_PER_DAY) + nofd + NANOS_PER_DAY) % NANOS_PER_DAY;
        if (nofd == newNofd) {
            return this;
        }
        HourOfDay newHour = HourOfDay.hourOfDay((int) (newNofd / NANOS_PER_HOUR));
        MinuteOfHour newMinute = MinuteOfHour.minuteOfHour((int) ((newNofd / NANOS_PER_MINUTE) % MINUTES_PER_HOUR));
        SecondOfMinute newSecond = SecondOfMinute.secondOfMinute((int) ((newNofd / NANOS_PER_SECOND) % SECONDS_PER_MINUTE));
        NanoOfSecond newNano = NanoOfSecond.nanoOfSecond((int) (newNofd % NANOS_PER_SECOND));
        return time(newHour, newMinute, newSecond, newNano);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this LocalTime with the specified period subtracted.
     * <p>
     * This subtracts the amount in hours, minutes and seconds from the specified period from this time.
     * Any date amounts, such as years, months or days are ignored.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param periodProvider  the period to subtract, not null
     * @return a new updated LocalTime, never null
     */
    public LocalTime minus(PeriodProvider periodProvider) {
        Period period = Period.period(periodProvider);
        // safe from overflow
        long totalNanos = period.getHours() * NANOS_PER_HOUR +
                period.getMinutes() * NANOS_PER_MINUTE +
                period.getSeconds() * NANOS_PER_SECOND;
        return minusNanos(totalNanos).minusNanos(period.getNanos());
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this LocalTime with the specified period in hours subtracted.
     * <p>
     * If the resulting hour is lesser than 0 or greater than 23, the field <b>rolls</b>.
     * For instance, 24 becomes 0 and -1 becomes 23.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hours  the hours to subtract, may be negative
     * @return a new updated LocalTime, never null
     */
    public LocalTime minusHours(int hours) {
        if (hours == 0) {
            return this;
        }
        int newHour = (-(hours % HOURS_PER_DAY) + hour.getValue() + HOURS_PER_DAY) % HOURS_PER_DAY;
        return withHourOfDay(newHour);
    }

    /**
     * Returns a copy of this LocalTime with the specified period in minutes subtracted.
     * <p>
     * If the resulting hour is lesser than 0 or greater than 23, the hour field <b>rolls</b>.
     * For instance, 24 becomes 0 and -1 becomes 23.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param minutes  the minutes to subtract, may be negative
     * @return a new updated LocalTime, never null
     */
    public LocalTime minusMinutes(int minutes) {
        if (minutes == 0) {
            return this;
        }
        int mofd = hour.getValue() * MINUTES_PER_HOUR + minute.getValue();
        int newMofd = (-(minutes % MINUTES_PER_DAY) + mofd + MINUTES_PER_DAY) % MINUTES_PER_DAY;
        if (mofd == newMofd) {
            return this;
        }
        HourOfDay newHour = HourOfDay.hourOfDay(newMofd / MINUTES_PER_HOUR);
        MinuteOfHour newMinute = MinuteOfHour.minuteOfHour(newMofd % MINUTES_PER_HOUR);
        return time(newHour, newMinute, second, nano);
    }

    /**
     * Returns a copy of this LocalTime with the specified period in seconds subtracted.
     * <p>
     * If the resulting hour is lesser than 0 or greater than 23, the hour field <b>rolls</b>.
     * For instance, 24 becomes 0 and -1 becomes 23.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param seconds  the seconds to subtract, may be negative
     * @return a new updated LocalTime, never null
     */
    public LocalTime minusSeconds(int seconds) {
        if (seconds == 0) {
            return this;
        }
        int sofd = hour.getValue() * SECONDS_PER_HOUR +
                    minute.getValue() * SECONDS_PER_MINUTE + second.getValue();
        int newSofd = (-(seconds % SECONDS_PER_DAY) + sofd + SECONDS_PER_DAY) % SECONDS_PER_DAY;
        if (sofd == newSofd) {
            return this;
        }
        HourOfDay newHour = HourOfDay.hourOfDay(newSofd / SECONDS_PER_HOUR);
        MinuteOfHour newMinute = MinuteOfHour.minuteOfHour((newSofd / SECONDS_PER_MINUTE) % MINUTES_PER_HOUR);
        SecondOfMinute newSecond = SecondOfMinute.secondOfMinute(newSofd % SECONDS_PER_MINUTE);
        return time(newHour, newMinute, newSecond, nano);
    }

    /**
     * Returns a copy of this LocalTime with the specified period in nanoseconds subtracted.
     * <p>
     * If the resulting hour is lesser than 0 or greater than 23, the hour field <b>rolls</b>.
     * For instance, 24 becomes 0 and -1 becomes 23.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param nanos  the nanos to subtract, may be negative
     * @return a new updated LocalTime, never null
     */
    public LocalTime minusNanos(long nanos) {
        if (nanos == 0) {
            return this;
        }
        long nofd = toNanoOfDay();
        long newNofd = (-(nanos % NANOS_PER_DAY) + nofd + NANOS_PER_DAY) % NANOS_PER_DAY;
        if (nofd == newNofd) {
            return this;
        }
        HourOfDay newHour = HourOfDay.hourOfDay((int) (newNofd / NANOS_PER_HOUR));
        MinuteOfHour newMinute = MinuteOfHour.minuteOfHour((int) ((newNofd / NANOS_PER_MINUTE) % MINUTES_PER_HOUR));
        SecondOfMinute newSecond = SecondOfMinute.secondOfMinute((int) ((newNofd / NANOS_PER_SECOND) % SECONDS_PER_MINUTE));
        NanoOfSecond newNano = NanoOfSecond.nanoOfSecond((int) (newNofd % NANOS_PER_SECOND));
        return time(newHour, newMinute, newSecond, newNano);
    }

    //-----------------------------------------------------------------------
    /**
     * Checks whether the time matches the specified matcher.
     * <p>
     * Matchers can be used to query the time.
     * A simple matcher might simply query one of the fields, such as the hour field.
     * A more complex matcher might query if the time is during opening hours.
     *
     * @param matcher  the matcher to use, not null
     * @return true if this time matches the matcher, false otherwise
     */
    public boolean matches(TimeMatcher matcher) {
        return matcher.matchesTime(this);
    }

    //-----------------------------------------------------------------------
    /**
     * Adjusts a time to have the value of this time.
     *
     * @param time  the time to be adjusted, not null
     * @return the adjusted time, never null
     */
    public LocalTime adjustTime(LocalTime time) {
        return matchesTime(time) ? time : this;
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if this time is equal to the input time
     *
     * @param time the time to match, not null
     * @return true if the two times are equal, false otherwise
     */
    public boolean matchesTime(LocalTime time) {
        return hour.equals(time.hour) && minute.equals(time.minute) &&
                second.equals(time.second) && nano.equals(time.nano);
    }

    //-----------------------------------------------------------------------
    /**
     * Converts this time to a <code>DateTimeFields</code> containing the
     * hour, minute, second and nano fields.
     *
     * @return the field set, never null
     */
    public DateTimeFields toDateTimeFields() {
        Map<DateTimeFieldRule, Integer> map = new HashMap<DateTimeFieldRule, Integer>();
        map.put(ISOChronology.hourOfDayRule(), hour.getValue());
        map.put(ISOChronology.minuteOfHourRule(), minute.getValue());
        map.put(ISOChronology.secondOfMinuteRule(), second.getValue());
        map.put(ISOChronology.nanoOfSecondRule(), nano.getValue());
        return DateTimeFields.fields(map);
    }

    /**
     * Converts this time to a <code>LocalTime</code>, trivially
     * returning <code>this</code>.
     *
     * @return <code>this</code>, never null
     */
    public LocalTime toLocalTime() {
        return this;
    }

    /**
     * Converts this date to a <code>Calendrical</code>.
     *
     * @return the calendrical representation for this instance, never null
     */
    public Calendrical toCalendrical() {
        return Calendrical.calendrical(null, this, null, null);
    }

    //-----------------------------------------------------------------------
    /**
     * Extracts the time as seconds of day,
     * from <code>0</code> to <code>24 * 60 * 60 - 1</code>.
     *
     * @return the second of day equivalent to this time
     */
    int toSecondOfDay() {
        if (this == MIDNIGHT) {
            return 0;
        }
        int total = hour.getValue() * SECONDS_PER_HOUR;
        total += minute.getValue() * SECONDS_PER_MINUTE;
        total += second.getValue();
        return total;
    }

    /**
     * Extracts the time as millis of day,
     * from <code>0</code> to <code>24 * 60 * 60 * 1000 - 1</code>.
     *
     * @return the milli of day equivalent to this time
     */
    int toMilliOfDay() {
        if (this == MIDNIGHT) {
            return 0;
        }
        long total = toNanoOfDay();
        return (int) (total / 1000000);
    }

    /**
     * Extracts the time as nanos of day,
     * from <code>0</code> to <code>24 * 60 * 60 * 1,000,000,000 - 1</code>.
     *
     * @return the nano of day equivalent to this time
     */
    public long toNanoOfDay() {
        if (this == MIDNIGHT) {
            return 0;
        }
        long total = hour.getValue() * NANOS_PER_HOUR;
        total += minute.getValue() * NANOS_PER_MINUTE;
        total += second.getValue() * NANOS_PER_SECOND;
        total += nano.getValue();
        return total;
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
        int cmp = hour.compareTo(other.hour);
        if (cmp == 0) {
            cmp = minute.compareTo(other.minute);
            if (cmp == 0) {
                cmp = second.compareTo(other.second);
                if (cmp == 0) {
                    cmp = nano.compareTo(other.nano);
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
            return matchesTime(localTime);
        }
        return false;
    }

    /**
     * A hash code for this time.
     *
     * @return a suitable hash code
     */
    @Override
    public int hashCode() {
        long nod = toNanoOfDay();
        return (int) (nod ^ (nod >>> 32));
    }

    //-----------------------------------------------------------------------
    /**
     * Outputs the time as a <code>String</code>, such as '10:15'.
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
        int hourValue = hour.getValue();
        int minuteValue = minute.getValue();
        int secondValue = second.getValue();
        int nanoValue = nano.getValue();
        buf.append(hourValue < 10 ? "0" : "").append(hourValue)
            .append(minuteValue < 10 ? ":0" : ":").append(minuteValue);
        if (secondValue > 0 || nanoValue > 0) {
            buf.append(secondValue < 10 ? ":0" : ":").append(secondValue);
            if (nanoValue > 0) {
                buf.append('.');
                if (nanoValue % 1000000 == 0) {
                    buf.append(Integer.toString((nanoValue / 1000000) + 1000).substring(1));
                } else if (nanoValue % 1000 == 0) {
                    buf.append(Integer.toString((nanoValue / 1000) + 1000000).substring(1));
                } else {
                    buf.append(Integer.toString((nanoValue) + 1000000000).substring(1));
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
     * This adds the amount in hours, minutes and seconds from the specified period to this time.
     * Any date amounts, such as years, months or days are ignored.
     * <p>
     * This method returns an {@link Overflow} instance with the result of the
     * addition and any overflow in days.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param periodProvider  the period to add, not null
     * @return an Overflow instance with the resulting time and overflow, never null
     */
    Overflow plusWithOverflow(PeriodProvider periodProvider) {
        Period period = Period.period(periodProvider);
        // safe from overflow
        long totalNanos = period.getHours() * NANOS_PER_HOUR +
                period.getMinutes() * NANOS_PER_MINUTE +
                period.getSeconds() * NANOS_PER_SECOND;
        return plusNanosWithOverflow(totalNanos);  // TODO period.getNanos()
    }

    /**
     * Returns a copy of this LocalTime with the specified period added,
     * returning the new time with any overflow in days.
     * <p>
     * This method returns an {@link Overflow} instance with the result of the
     * addition and any overflow in days.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hours  the hours to add, may be negative
     * @param minutes the minutes to add, may be negative
     * @param seconds the seconds to add, may be negative
     * @param nanos the nanos to add, may be negative
     * @return an Overflow instance with the resulting time and overflow, never null
     */
    public Overflow plusWithOverflow(int hours, int minutes, int seconds, int nanos) {
        // safe from overflow
        long totalNanos = hours * NANOS_PER_HOUR + minutes * NANOS_PER_MINUTE +
                seconds * NANOS_PER_SECOND + nanos;
        return plusNanosWithOverflow(totalNanos);
    }

    /**
     * Returns a copy of this LocalTime with the specified period in nanos added,
     * returning any overflow in days.
     * <p>
     * This method returns an {@link Overflow} instance with the result of the
     * addition and any overflow in days.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param nanos the nanos to add, may be negative
     * @return an Overflow instance with the resulting time and overflow, never null
     */
    public Overflow plusNanosWithOverflow(long nanos) {
        if (nanos == 0) {
            return new Overflow(this, 0);
        }
        long thisNanos = toNanoOfDay();
        long nanosSum = MathUtils.safeAdd(thisNanos, nanos);
        int days = (int) (nanosSum / NANOS_PER_DAY);
        long newNanos = nanosSum % NANOS_PER_DAY;
        if (newNanos < 0) {
            days--;
            newNanos += NANOS_PER_DAY;
        }
        LocalTime newTime = newNanos == thisNanos ? this : fromNanoOfDay(newNanos);
        return new Overflow(newTime, days);
    }

    //-----------------------------------------------------------------------
    /**
     * Subtracts the specified period to create a new LocalTime returning any
     * overflow in days.
     * <p>
     * This subtracts the amount in hours, minutes and seconds from the specified period from this time.
     * Any date amounts, such as years, months or days are ignored.
     * <p>
     * This method returns an {@link Overflow} instance with the result of the
     * subtraction and any overflow in days.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param periodProvider  the period to subtract, not null
     * @return an Overflow instance with the resulting time and overflow, never null
     */
    Overflow minusWithOverflow(PeriodProvider periodProvider) {
        Period period = Period.period(periodProvider);
        // safe from overflow
        long totalNanos = period.getHours() * NANOS_PER_HOUR +
                period.getMinutes() * NANOS_PER_MINUTE +
                period.getSeconds() * NANOS_PER_SECOND;
        return minusNanosWithOverflow(totalNanos);  // TODO period.getNanos()
    }

    /**
     * Returns a copy of this LocalTime with the specified period subtracted,
     * returning the new time with any overflow in days.
     * <p>
     * This method returns an {@link Overflow} instance with the result of the
     * subtraction and any overflow in days.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hours  the hours to subtract, may be negative
     * @param minutes the minutes to subtract, may be negative
     * @param seconds the seconds to subtract, may be negative
     * @param nanos the nanos to subtract, may be negative
     * @return an Overflow instance with the resulting time and overflow, never null
     */
    public Overflow minusWithOverflow(int hours, int minutes, int seconds, int nanos) {
        // safe from overflow
        long totalNanos = hours * NANOS_PER_HOUR + minutes * NANOS_PER_MINUTE +
                seconds * NANOS_PER_SECOND + nanos;
        return minusNanosWithOverflow(totalNanos);
    }

    /**
     * Returns a copy of this LocalTime with the specified period in nanos subtracted,
     * returning any overflow in days.
     * <p>
     * This method returns an {@link Overflow} instance with the result of the
     * addition and any overflow in days.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param nanos the nanos to subtract, may be negative
     * @return a new updated Overflow, never null
     */
    public Overflow minusNanosWithOverflow(long nanos) {
        if (nanos == 0) {
            return new Overflow(this, 0);
        }
        long thisNanos = toNanoOfDay();
        long nanosSum = MathUtils.safeSubtract(thisNanos, nanos);
        int days = (int) (nanosSum / NANOS_PER_DAY);
        long newNanos = nanosSum % NANOS_PER_DAY;
        if (newNanos < 0) {
            days--;
            newNanos += NANOS_PER_DAY;
        }
        LocalTime newTime = newNanos == thisNanos ? this : fromNanoOfDay(newNanos);
        return new Overflow(newTime, days);
    }

    //-----------------------------------------------------------------------
    /**
     * The result of addition to a LocalTime allowing the expression of
     * any overflow in days.
     */
    public static final class Overflow {
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

//        /**
//         * Fulfils the TimeProvider interface by returning the result time.
//         *
//         * @return the result time, never null
//         */
//        public LocalTime toLocalTime() {
//            return time;
//        }

        /**
         * Creates a LocalDateTime from the specified date and this instance.
         *
         * @param date  the date to use, not null
         * @return the combination of the date, time and overflow in days, never null
         */
        public LocalDateTime toLocalDateTime(LocalDate date) {
            return LocalDateTime.dateTime(date.plusDays(getOverflowDays()), time);
        }

        /**
         * Compares this object to another.
         *
         * @param obj  the object to compare to
         * @return true if equal
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj instanceof Overflow) {
                Overflow other = (Overflow) obj;
                return time.equals(other.time) && days == other.days;
            }
            return false;
        }

        /**
         * Returns a suitable hash code.
         *
         * @return the hash code
         */
        @Override
        public int hashCode() {
            return time.hashCode() + days;
        }

        /**
         * Returns a string description of this instance.
         *
         * @return the string, never null
         */
        @Override
        public String toString() {
            return getResultTime().toString() + " + P" + days + "D";
        }
    }
}
