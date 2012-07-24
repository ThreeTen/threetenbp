/*
 * Copyright (c) 2007-2012, Stephen Colebourne & Michael Nascimento Santos
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

import static javax.time.DateTimes.NANOS_PER_SECOND;
import static javax.time.calendrical.LocalDateTimeField.NANO_OF_DAY;

import java.io.Serializable;

import javax.time.calendrical.AdjustableDateTime;
import javax.time.calendrical.DateTime;
import javax.time.calendrical.DateTimeAdjuster;
import javax.time.calendrical.DateTimeBuilder;
import javax.time.calendrical.DateTimeField;
import javax.time.calendrical.LocalDateTimeField;
import javax.time.calendrical.LocalPeriodUnit;
import javax.time.calendrical.PeriodUnit;
import javax.time.format.CalendricalFormatter;
import javax.time.format.CalendricalParseException;
import javax.time.format.DateTimeFormatters;

/**
 * A time with a zone offset from UTC in the ISO-8601 calendar system,
 * such as {@code 10:15:30+01:00}.
 * <p>
 * {@code OffsetTime} is an immutable calendrical that represents a time, often
 * viewed as hour-minute-second-offset.
 * This class stores all time fields, to a precision of nanoseconds,
 * as well as a zone offset.
 * For example, the value "13:45.30.123456789+02:00" can be stored
 * in a {@code OffsetTime}.
 * 
 * <h4>Implementation notes</h4>
 * This class is immutable and thread-safe.
 */
public final class OffsetTime
        implements AdjustableDateTime, DateTimeAdjuster, Comparable<OffsetTime>, Serializable {

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = -1751032571L;

    /**
     * The local time, not null.
     */
    private final LocalTime time;
    /**
     * The zone offset from UTC, not null.
     */
    private final ZoneOffset offset;

    //-----------------------------------------------------------------------
    /**
     * Obtains the current time from the system clock in the default time-zone.
     * <p>
     * This will query the {@link Clock#systemDefaultZone() system clock} in the default
     * time-zone to obtain the current time.
     * The offset will be calculated from the time-zone in the clock.
     * <p>
     * Using this method will prevent the ability to use an alternate clock for testing
     * because the clock is hard-coded.
     *
     * @return the current time using the system clock, not null
     */
    public static OffsetTime now() {
        return now(Clock.systemDefaultZone());
    }

    /**
     * Obtains the current time from the specified clock.
     * <p>
     * This will query the specified clock to obtain the current time.
     * The offset will be calculated from the time-zone in the clock.
     * <p>
     * Using this method allows the use of an alternate clock for testing.
     * The alternate clock may be introduced using {@link Clock dependency injection}.
     *
     * @param clock  the clock to use, not null
     * @return the current time, not null
     */
    public static OffsetTime now(Clock clock) {
        DateTimes.checkNotNull(clock, "Clock must not be null");
        final Instant now = clock.instant();  // called once
        return ofInstant(now, clock.getZone().getRules().getOffset(now));
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code OffsetTime} from an hour and minute.
     * <p>
     * The second and nanosecond fields will be set to zero by this factory method.
     *
     * @param hour  the hour-of-day to represent, from 0 to 23
     * @param minute  the minute-of-hour to represent, from 0 to 59
     * @param offset  the zone offset, not null
     * @return the offset time, not null
     * @throws CalendricalException if the value of any field is out of range
     */
    public static OffsetTime of(int hour, int minute, ZoneOffset offset) {
        LocalTime time = LocalTime.of(hour, minute);
        return new OffsetTime(time, offset);
    }

    /**
     * Obtains an instance of {@code OffsetTime} from an hour, minute and second.
     * <p>
     * The second field will be set to zero by this factory method.
     *
     * @param hour  the hour-of-day to represent, from 0 to 23
     * @param minute  the minute-of-hour to represent, from 0 to 59
     * @param second  the second-of-minute to represent, from 0 to 59
     * @param offset  the zone offset, not null
     * @return the offset time, not null
     * @throws CalendricalException if the value of any field is out of range
     */
    public static OffsetTime of(int hour, int minute, int second, ZoneOffset offset) {
        LocalTime time = LocalTime.of(hour, minute, second);
        return new OffsetTime(time, offset);
    }

    /**
     * Obtains an instance of {@code OffsetTime} from an hour, minute, second and nanosecond.
     *
     * @param hour  the hour-of-day to represent, from 0 to 23
     * @param minute  the minute-of-hour to represent, from 0 to 59
     * @param second  the second-of-minute to represent, from 0 to 59
     * @param nanoOfSecond  the nano-of-second to represent, from 0 to 999,999,999
     * @param offset  the zone offset, not null
     * @return the offset time, not null
     * @throws CalendricalException if the value of any field is out of range
     */
    public static OffsetTime of(int hour, int minute, int second, int nanoOfSecond, ZoneOffset offset) {
        LocalTime time = LocalTime.of(hour, minute, second, nanoOfSecond);
        return new OffsetTime(time, offset);
    }

    /**
     * Obtains an instance of {@code OffsetTime} from a local time and an offset.
     *
     * @param time  the local time, not null
     * @param offset  the zone offset, not null
     * @return the offset time, not null
     */
    public static OffsetTime of(LocalTime time, ZoneOffset offset) {
        return new OffsetTime(time, offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code OffsetTime} from an {@code Instant}.
     * <p>
     * The date component of the instant is dropped during the conversion.
     * This means that the conversion can never fail due to the instant being
     * out of the valid range of dates.
     *
     * @param instant  the instant to create the time from, not null
     * @param offset  the zone offset to use, not null
     * @return the offset time, not null
     */
    public static OffsetTime ofInstant(Instant instant, ZoneOffset offset) {
        DateTimes.checkNotNull(instant, "Instant must not be null");
        DateTimes.checkNotNull(offset, "ZoneOffset must not be null");
        long secsOfDay = instant.getEpochSecond() % DateTimes.SECONDS_PER_DAY;
        secsOfDay = (secsOfDay + offset.getTotalSeconds()) % DateTimes.SECONDS_PER_DAY;
        if (secsOfDay < 0) {
            secsOfDay += DateTimes.SECONDS_PER_DAY;
        }
        LocalTime time = LocalTime.ofSecondOfDay(secsOfDay, instant.getNano());
        return new OffsetTime(time, offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code OffsetTime} from a calendrical.
     * <p>
     * A calendrical represents some form of date and time information.
     * This factory converts the arbitrary calendrical to an instance of {@code OffsetTime}.
     * 
     * @param calendrical  the calendrical to convert, not null
     * @return the offset time, not null
     * @throws CalendricalException if unable to convert to an {@code OffsetTime}
     */
    public static OffsetTime from(DateTime calendrical) {
        OffsetTime obj = calendrical.extract(OffsetTime.class);
        return DateTimes.ensureNotNull(obj, "Unable to convert calendrical to OffsetTime: ", calendrical.getClass());
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code OffsetTime} from a text string such as {@code 10:15:30+01:00}.
     * <p>
     * The string must represent a valid time and is parsed using
     * {@link javax.time.format.DateTimeFormatters#isoOffsetTime()}.
     *
     * @param text  the text to parse such as "10:15:30+01:00", not null
     * @return the parsed local time, not null
     * @throws CalendricalParseException if the text cannot be parsed
     */
    public static OffsetTime parse(CharSequence text) {
        return parse(text, DateTimeFormatters.isoOffsetTime());
    }

    /**
     * Obtains an instance of {@code OffsetTime} from a text string using a specific formatter.
     * <p>
     * The text is parsed using the formatter, returning a time.
     *
     * @param text  the text to parse, not null
     * @param formatter  the formatter to use, not null
     * @return the parsed offset time, not null
     * @throws CalendricalParseException if the text cannot be parsed
     */
    public static OffsetTime parse(CharSequence text, CalendricalFormatter formatter) {
        DateTimes.checkNotNull(formatter, "CalendricalFormatter must not be null");
        return formatter.parse(text, OffsetTime.class);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor.
     *
     * @param time  the time, validated as not null
     * @param offset  the zone offset, validated as not null
     */
    private OffsetTime(LocalTime time, ZoneOffset offset) {
        if (time == null) {
            throw new NullPointerException("LocalTime must not be null");
        }
        if (offset == null) {
            throw new NullPointerException("ZoneOffset must not be null");
        }
        this.time = time;
        this.offset = offset;
    }

    /**
     * Returns a new time based on this one, returning {@code this} where possible.
     *
     * @param time  the time to create with, not null
     * @param offset  the zone offset to create with, not null
     */
    private OffsetTime with(LocalTime time, ZoneOffset offset) {
        if (this.time == time && this.offset.equals(offset)) {
            return this;
        }
        return new OffsetTime(time, offset);
    }

    //-----------------------------------------------------------------------
    @Override
    public long get(DateTimeField field) {
        if (field instanceof LocalDateTimeField) {
            return time.get(field);
        }
        return field.doGet(this);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the zone offset representing how far ahead or behind UTC the time is.
     *
     * @return the zone offset, not null
     */
    public ZoneOffset getOffset() {
        return offset;
    }

    /**
     * Returns a copy of this {@code OffsetTime} with the specified offset ensuring
     * that the result has the same local time.
     * <p>
     * This method returns an object with the same {@code LocalTime} and the specified {@code ZoneOffset}.
     * No calculation is needed or performed.
     * For example, if this time represents {@code 10:30+02:00} and the offset specified is
     * {@code +03:00}, then this method will return {@code 10:30+03:00}.
     * <p>
     * To take into account the difference between the offsets, and adjust the time fields,
     * use {@link #withOffsetSameInstant}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param offset  the zone offset to change to, not null
     * @return an {@code OffsetTime} based on this time with the requested offset, not null
     */
    public OffsetTime withOffsetSameLocal(ZoneOffset offset) {
        return offset != null && offset.equals(this.offset) ? this : new OffsetTime(time, offset);
    }

    /**
     * Returns a copy of this {@code OffsetTime} with the specified offset ensuring
     * that the result is at the same instant on an implied day.
     * <p>
     * This method returns an object with the specified {@code ZoneOffset} and a {@code LocalTime}
     * adjusted by the difference between the two offsets.
     * This will result in the old and new objects representing the same instant an an implied day.
     * This is useful for finding the local time in a different offset.
     * For example, if this time represents {@code 10:30+02:00} and the offset specified is
     * {@code +03:00}, then this method will return {@code 11:30+03:00}.
     * <p>
     * To change the offset without adjusting the local time use {@link #withOffsetSameLocal}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param offset  the zone offset to change to, not null
     * @return an {@code OffsetTime} based on this time with the requested offset, not null
     */
    public OffsetTime withOffsetSameInstant(ZoneOffset offset) {
        if (offset.equals(this.offset)) {
            return this;
        }
        int difference = offset.getTotalSeconds() - this.offset.getTotalSeconds();
        LocalTime adjusted = time.plusSeconds(difference);
        return new OffsetTime(adjusted, offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the hour-of-day field.
     *
     * @return the hour-of-day, from 0 to 23
     */
    public int getHour() {
        return time.getHour();
    }

    /**
     * Gets the minute-of-hour field.
     *
     * @return the minute-of-hour, from 0 to 59
     */
    public int getMinute() {
        return time.getMinute();
    }

    /**
     * Gets the second-of-minute field.
     *
     * @return the second-of-minute, from 0 to 59
     */
    public int getSecond() {
        return time.getSecond();
    }

    /**
     * Gets the nano-of-second field.
     *
     * @return the nano-of-second, from 0 to 999,999,999
     */
    public int getNano() {
        return time.getNano();
    }

    //-----------------------------------------------------------------------
    /**
     * Returns an adjusted time based on this time.
     * <p>
     * This adjusts the time according to the rules of the specified adjuster.
     * A simple adjuster might simply set the one of the fields, such as the hour field.
     * A more complex adjuster might set the time to the last hour of the day.
     * The adjuster is responsible for handling special cases, such as the varying
     * lengths of month and leap years.
     * <p>
     * In addition, all principal classes implement the {@link DateTimeAdjuster} interface,
     * including this one. For example, {@link AmPm} implements the adjuster interface.
     * As such, this code will compile and run:
     * <pre>
     *  time.with(AmPm.PM);
     * </pre>
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param adjuster the adjuster to use, not null
     * @return an {@code OffsetTime} based on this time with the adjustment made, not null
     * @throws CalendricalException if the adjustment cannot be made
     */
    public OffsetTime with(DateTimeAdjuster adjuster) {
        if (adjuster instanceof LocalTime) {
            return with((LocalTime) adjuster, offset);
        } else if (adjuster instanceof OffsetTime) {
            return with(((OffsetTime) adjuster).toLocalTime(), offset);
        }
        return (OffsetTime) adjuster.doAdjustment(this);
    }

    /**
     * Returns a copy of this time with the specified field altered.
     * <p>
     * This method returns a new time based on this time with a new value for the specified field.
     * This can be used to change any field, for example to set the hour-of-day.
     * The offset is not part of the calculation and will be unchanged in the result.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param field  the field to set in the returned time, not null
     * @param newValue  the new value of the field in the returned time, not null
     * @return an {@code OffsetTime} based on this time with the specified field set, not null
     * @throws CalendricalException if the value is invalid
     */
    public OffsetTime with(DateTimeField field, long newValue) {
        if (field instanceof LocalDateTimeField) {
            return with(time.with(field, newValue), offset);
        }
        return field.doSet(this, newValue);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this {@code OffsetTime} with the hour-of-day value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hour  the hour-of-day to represent, from 0 to 23
     * @return an {@code OffsetTime} based on this time with the requested hour, not null
     * @throws CalendricalException if the hour value is invalid
     */
    public OffsetTime withHour(int hour) {
        return with(time.withHour(hour), offset);
    }

    /**
     * Returns a copy of this {@code OffsetTime} with the minute-of-hour value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param minute  the minute-of-hour to represent, from 0 to 59
     * @return an {@code OffsetTime} based on this time with the requested minute, not null
     * @throws CalendricalException if the minute value is invalid
     */
    public OffsetTime withMinute(int minute) {
        return with(time.withMinute(minute), offset);
    }

    /**
     * Returns a copy of this {@code OffsetTime} with the second-of-minute value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param second  the second-of-minute to represent, from 0 to 59
     * @return an {@code OffsetTime} based on this time with the requested second, not null
     * @throws CalendricalException if the second value is invalid
     */
    public OffsetTime withSecond(int second) {
        return with(time.withSecond(second), offset);
    }

    /**
     * Returns a copy of this {@code OffsetTime} with the nano-of-second value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param nanoOfSecond  the nano-of-second to represent, from 0 to 999,999,999
     * @return an {@code OffsetTime} based on this time with the requested nanosecond, not null
     * @throws CalendricalException if the nanos value is invalid
     */
    public OffsetTime withNano(int nanoOfSecond) {
        return with(time.withNano(nanoOfSecond), offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this time with the specified duration added.
     * <p>
     * This adds the specified duration to this time, returning a new time.
     * The calculation wraps around midnight.
     * <p>
     * The calculation is equivalent to using {@link #plusSeconds(long)} and
     * {@link #plusNanos(long)} on the two parts of the duration.
     * The offset is not part of the calculation and will be unchanged in the result.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param duration  the duration to add, not null
     * @return an {@code OffsetTime} based on this time with the duration added, not null
     */
    public OffsetTime plus(Duration duration) {
        return with(time.plus(duration), offset);
    }

    /**
     * Returns a copy of this time with the specified period added.
     * <p>
     * This method returns a new time based on this time with the specified period added.
     * The calculation is delegated to the unit within the period.
     * The offset is not part of the calculation and will be unchanged in the result.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param period  the period to add, not null
     * @return an {@code OffsetTime} based on this time with the period added, not null
     * @throws CalendricalException if the unit cannot be added to this type
     */
    public OffsetTime plus(Period period) {
        return plus(period.getAmount(), period.getUnit());
    }

    /**
     * Returns a copy of this time with the specified period added.
     * <p>
     * This method returns a new time based on this time with the specified period added.
     * This can be used to add any period that is defined by a unit, for example to add hours, minutes or seconds.
     * The unit is responsible for the details of the calculation, including the resolution
     * of any edge cases in the calculation.
     * The offset is not part of the calculation and will be unchanged in the result.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param periodAmount  the amount of the unit to add to the returned time, not null
     * @param unit  the unit of the period to add, not null
     * @return an {@code OffsetTime} based on this time with the specified period added, not null
     * @throws CalendricalException if the unit cannot be added to this type
     */
    public OffsetTime plus(long periodAmount, PeriodUnit unit) {
        if (unit instanceof LocalPeriodUnit) {
            return with(time.plus(periodAmount, unit), offset);
        }
        return unit.doAdd(this, periodAmount);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this {@code OffsetTime} with the specified period in hours added.
     * <p>
     * This adds the specified number of hours to this time, returning a new time.
     * The calculation wraps around midnight.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hours  the hours to add, may be negative
     * @return an {@code OffsetTime} based on this time with the hours added, not null
     */
    public OffsetTime plusHours(long hours) {
        return with(time.plusHours(hours), offset);
    }

    /**
     * Returns a copy of this {@code OffsetTime} with the specified period in minutes added.
     * <p>
     * This adds the specified number of minutes to this time, returning a new time.
     * The calculation wraps around midnight.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param minutes  the minutes to add, may be negative
     * @return an {@code OffsetTime} based on this time with the minutes added, not null
     */
    public OffsetTime plusMinutes(long minutes) {
        return with(time.plusMinutes(minutes), offset);
    }

    /**
     * Returns a copy of this {@code OffsetTime} with the specified period in seconds added.
     * <p>
     * This adds the specified number of seconds to this time, returning a new time.
     * The calculation wraps around midnight.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param seconds  the seconds to add, may be negative
     * @return an {@code OffsetTime} based on this time with the seconds added, not null
     */
    public OffsetTime plusSeconds(long seconds) {
        return with(time.plusSeconds(seconds), offset);
    }

    /**
     * Returns a copy of this {@code OffsetTime} with the specified period in nanoseconds added.
     * <p>
     * This adds the specified number of nanoseconds to this time, returning a new time.
     * The calculation wraps around midnight.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param nanos  the nanos to add, may be negative
     * @return an {@code OffsetTime} based on this time with the nanoseconds added, not null
     */
    public OffsetTime plusNanos(long nanos) {
        return with(time.plusNanos(nanos), offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this time with the specified duration subtracted.
     * <p>
     * This subtracts the specified duration to this time, returning a new time.
     * The calculation wraps around midnight.
     * <p>
     * The calculation is equivalent to using {@link #minusSeconds(long)} and
     * {@link #minusNanos(long)} on the two parts of the duration.
     * The offset is not part of the calculation and will be unchanged in the result.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param duration  the duration to subtract, not null
     * @return an {@code OffsetTime} based on this time with the duration subtracted, not null
     */
    public OffsetTime minus(Duration duration) {
        return with(time.minus(duration), offset);
    }

    /**
     * Returns a copy of this time with the specified period subtracted.
     * <p>
     * This method returns a new time based on this time with the specified period subtracted.
     * The calculation is delegated to the unit within the period.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param period  the period to subtract, not null
     * @return an {@code OffsetTime} based on this time with the period subtracted, not null
     * @throws CalendricalException if the unit cannot be added to this type
     */
    public OffsetTime minus(Period period) {
        return minus(period.getAmount(), period.getUnit());
    }

    /**
     * Returns a copy of this time with the specified period subtracted.
     * <p>
     * This method returns a new time based on this time with the specified period subtracted.
     * This can be used to subtract any period that is defined by a unit, for example to subtract hours, minutes or seconds.
     * The unit is responsible for the details of the calculation, including the resolution
     * of any edge cases in the calculation.
     * The offset is not part of the calculation and will be unchanged in the result.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param periodAmount  the amount of the unit to subtract from the returned time, not null
     * @param unit  the unit of the period to subtract, not null
     * @return an {@code OffsetTime} based on this time with the specified period subtracted, not null
     * @throws CalendricalException if the unit cannot be added to this type
     */
    public OffsetTime minus(long periodAmount, PeriodUnit unit) {
        return plus(DateTimes.safeNegate(periodAmount), unit);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this {@code OffsetTime} with the specified period in hours subtracted.
     * <p>
     * This subtracts the specified number of hours from this time, returning a new time.
     * The calculation wraps around midnight.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hours  the hours to subtract, may be negative
     * @return an {@code OffsetTime} based on this time with the hours subtracted, not null
     */
    public OffsetTime minusHours(long hours) {
        return with(time.minusHours(hours), offset);
    }

    /**
     * Returns a copy of this {@code OffsetTime} with the specified period in minutes subtracted.
     * <p>
     * This subtracts the specified number of minutes from this time, returning a new time.
     * The calculation wraps around midnight.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param minutes  the minutes to subtract, may be negative
     * @return an {@code OffsetTime} based on this time with the minutes subtracted, not null
     */
    public OffsetTime minusMinutes(long minutes) {
        return with(time.minusMinutes(minutes), offset);
    }

    /**
     * Returns a copy of this {@code OffsetTime} with the specified period in seconds subtracted.
     * <p>
     * This subtracts the specified number of seconds from this time, returning a new time.
     * The calculation wraps around midnight.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param seconds  the seconds to subtract, may be negative
     * @return an {@code OffsetTime} based on this time with the seconds subtracted, not null
     */
    public OffsetTime minusSeconds(long seconds) {
        return with(time.minusSeconds(seconds), offset);
    }

    /**
     * Returns a copy of this {@code OffsetTime} with the specified period in nanoseconds subtracted.
     * <p>
     * This subtracts the specified number of nanoseconds from this time, returning a new time.
     * The calculation wraps around midnight.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param nanos  the nanos to subtract, may be negative
     * @return an {@code OffsetTime} based on this time with the nanoseconds subtracted, not null
     */
    public OffsetTime minusNanos(long nanos) {
        return with(time.minusNanos(nanos), offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Extracts date-time information in a generic way.
     * <p>
     * This method exists to fulfill the {@link DateTime} interface.
     * This implementation returns the following types:
     * <ul>
     * <li>LocalTime
     * <li>OffsetTime
     * <li>ZoneOffset
     * <li>DateTimeBuilder
     * <li>Class, returning {@code OffsetTime}
     * </ul>
     * 
     * @param <R> the type to extract
     * @param type  the type to extract, null returns null
     * @return the extracted object, null if unable to extract
     */
    @SuppressWarnings("unchecked")
    @Override
    public <R> R extract(Class<R> type) {
        if (type == OffsetTime.class) {
            return (R) this;
        } else if (type == LocalTime.class) {
            return (R) time;
        } else if (type == ZoneOffset.class) {
            return (R) offset;
        } else if (type == Class.class) {
            return (R) OffsetTime.class;
        } else if (type == DateTimeBuilder.class) {
            return (R) new DateTimeBuilder(this);
        }
        return null;
    }

    @Override
    public AdjustableDateTime doAdjustment(AdjustableDateTime calendrical) {
        return calendrical.with(NANO_OF_DAY, time.toNanoOfDay());
    }

    //-----------------------------------------------------------------------
    /**
     * Converts this time to a {@code LocalTime}.
     *
     * @return a LocalTime with the same time as this instance, not null
     */
    public LocalTime toLocalTime() {
        return time;
    }

    /**
     * Converts this time to epoch nanos based on 1970-01-01Z.
     * 
     * @return the epoch nanos value
     */
    private long toEpochNano() {
        long nod = time.toNanoOfDay();
        long offsetNanos = offset.getTotalSeconds() * NANOS_PER_SECOND;
        return nod - offsetNanos;
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this {@code OffsetTime} to another time based on the UTC equivalent times
     * then local time.
     * <p>
     * This ordering is consistent with {@code equals()}.
     * For example, the following is the comparator order:
     * <ol>
     * <li>{@code 10:30+01:00}</li>
     * <li>{@code 11:00+01:00}</li>
     * <li>{@code 12:00+02:00}</li>
     * <li>{@code 11:30+01:00}</li>
     * <li>{@code 12:00+01:00}</li>
     * <li>{@code 12:30+01:00}</li>
     * </ol>
     * Values #2 and #3 represent the same instant on the time-line.
     * When two values represent the same instant, the local time is compared
     * to distinguish them. This step is needed to make the ordering
     * consistent with {@code equals()}.
     *
     * @param other  the other time to compare to, not null
     * @return the comparator value, negative if less, positive if greater
     * @throws NullPointerException if {@code other} is null
     */
    public int compareTo(OffsetTime other) {
        if (offset.equals(other.offset)) {
            return time.compareTo(other.time);
        }
        int compare = DateTimes.safeCompare(toEpochNano(), other.toEpochNano());
        if (compare == 0) {
            compare = time.compareTo(other.time);
        }
        return compare;
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if the instant of this {@code OffsetTime} is after that of the
     * specified time applying both times to a common date.
     * <p>
     * This method differs from the comparison in {@link #compareTo} in that it
     * only compares the instant of the time. This is equivalent to converting both
     * times to an instant using the same date and comparing the instants.
     *
     * @param other  the other time to compare to, not null
     * @return true if this is after the instant of the specified time
     */
    public boolean isAfter(OffsetTime other) {
        return toEpochNano() > other.toEpochNano();
    }

    /**
     * Checks if the instant of this {@code OffsetTime} is before that of the
     * specified time applying both times to a common date.
     * <p>
     * This method differs from the comparison in {@link #compareTo} in that it
     * only compares the instant of the time. This is equivalent to converting both
     * times to an instant using the same date and comparing the instants.
     *
     * @param other  the other time to compare to, not null
     * @return true if this is before the instant of the specified time
     */
    public boolean isBefore(OffsetTime other) {
        return toEpochNano() < other.toEpochNano();
    }

    /**
     * Checks if the instant of this {@code OffsetTime} is equal to that of the
     * specified time applying both times to a common date.
     * <p>
     * This method differs from the comparison in {@link #compareTo} and {@link #equals}
     * in that it only compares the instant of the time. This is equivalent to converting both
     * times to an instant using the same date and comparing the instants.
     *
     * @param other  the other time to compare to, not null
     * @return true if this is equal to the instant of the specified time
     */
    public boolean equalInstant(OffsetTime other) {
        return toEpochNano() == other.toEpochNano();
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if this time is equal to another time.
     * <p>
     * The comparison is based on the local-time and the offset.
     * To compare for the same instant on the time-line, use {@link #equalInstant}.
     *
     * @param obj  the object to check, null returns false
     * @return true if this is equal to the other time
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof OffsetTime) {
            OffsetTime other = (OffsetTime) obj;
            return time.equals(other.time) && offset.equals(other.offset);
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
        return time.hashCode() ^ offset.hashCode();
    }

    //-----------------------------------------------------------------------
    /**
     * Outputs this time as a {@code String}, such as {@code 10:15:30+01:00}.
     * <p>
     * The output will be one of the following ISO-8601 formats:
     * <ul>
     * <li>{@code HH:mmXXXXX}</li>
     * <li>{@code HH:mm:ssXXXXX}</li>
     * <li>{@code HH:mm:ssfnnnXXXXX}</li>
     * <li>{@code HH:mm:ssfnnnnnnXXXXX}</li>
     * <li>{@code HH:mm:ssfnnnnnnnnnXXXXX}</li>
     * </ul>
     * The format used will be the shortest that outputs the full value of
     * the time where the omitted parts are implied to be zero.
     *
     * @return a string representation of this time, not null
     */
    @Override
    public String toString() {
        return time.toString() + offset.toString();
    }

    /**
     * Outputs this time as a {@code String} using the formatter.
     *
     * @param formatter  the formatter to use, not null
     * @return the formatted time string, not null
     * @throws UnsupportedOperationException if the formatter cannot print
     * @throws CalendricalException if an error occurs during printing
     */
    public String toString(CalendricalFormatter formatter) {
        DateTimes.checkNotNull(formatter, "CalendricalFormatter must not be null");
        return formatter.print(this);
    }

}
