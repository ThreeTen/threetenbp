/*
 * Copyright (c) 2012, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time.calendrical;

import static javax.time.DateTimes.HOURS_PER_DAY;
import static javax.time.DateTimes.MINUTES_PER_DAY;
import static javax.time.DateTimes.NANOS_PER_DAY;
import static javax.time.DateTimes.NANOS_PER_HOUR;
import static javax.time.DateTimes.NANOS_PER_MINUTE;
import static javax.time.DateTimes.NANOS_PER_SECOND;
import static javax.time.DateTimes.SECONDS_PER_DAY;

import javax.time.CalendricalException;
import javax.time.DateTimes;
import javax.time.Duration;
import javax.time.LocalDate;
import javax.time.LocalDateTime;
import javax.time.LocalTime;
import javax.time.Period;

/**
 * A standard set of time periods units.
 * <p>
 * These are the basic set of units common across many calendar systems.
 * The calculation part of the units is specific to the ISO calendar system,
 * however the units as concepts may be used as simple constants with other calendar systems.
 * 
 * <h4>Implementation notes</h4>
 * This is a final, immutable and thread-safe enum.
 */
public enum LocalTimeUnit implements PeriodUnit {

    /**
     * Unit that represents the concept of a nanosecond, the smallest supported unit of time.
     * For the ISO calendar system, it is equal to the 1,000,000,000th part of the second unit.
     */
    NANOS("Nanos", Duration.ofNanos(1)),
    /**
     * Unit that represents the concept of a microsecond.
     * For the ISO calendar system, it is equal to the 1,000,000th part of the second unit.
     */
    MICROS("Micros", Duration.ofNanos(1000)),
    /**
     * Unit that represents the concept of a millisecond.
     * For the ISO calendar system, it is equal to the 1000th part of the second unit.
     */
    MILLIS("Millis", Duration.ofNanos(1000000)),
    /**
     * Unit that represents the concept of a second.
     * For the ISO calendar system, it is equal to the second in the SI system
     * of units, except around a leap-second.
     */
    SECONDS("Seconds", Duration.ofSeconds(1)),
    /**
     * Unit that represents the concept of a minute.
     * For the ISO calendar system, it is equal to 60 seconds.
     */
    MINUTES("Minutes", Duration.ofSeconds(60)),
    /**
     * Unit that represents the concept of an hour.
     * For the ISO calendar system, it is equal to 60 minutes.
     */
    HOURS("Hours", Duration.ofSeconds(3600)),
    /**
     * Unit that represents the concept of half a day, as used in AM/PM.
     * For the ISO calendar system, it is equal to 12 hours.
     */
    HALF_DAYS("HalfDays", Duration.ofSeconds(43200));

    private static final long MICROS_PER_DAY = SECONDS_PER_DAY * 1000000L;
    private static final long MILLIS_PER_DAY = SECONDS_PER_DAY * 1000L;

    private final String name;
    private final Duration duration;

    private LocalTimeUnit(String name, Duration duration) {
        this.name = name;
        this.duration = duration;
    }

    //-----------------------------------------------------------------------
    @Override
    public String getName() {
        return name;
    }

    //-----------------------------------------------------------------------
    /**
     * Calculates the period in this unit between two times.
     * <p>
     * This will return the number of complete units between the local times.
     * If the second time is before the first, the result will be negative.
     * For example, {@code HOURS.between(time1, time2)} will calculate the difference in hours.
     * 
     * @param time1  the first time, not null
     * @param time2  the second time, not null
     * @return the period in terms of this unit, not null
     */
    public Period between(LocalTime time1, LocalTime time2) {
        return Period.of(periodBetween(time1, time2), this);
    }

    /**
     * Calculates the period in this unit between two date-times.
     * <p>
     * This will return the number of complete units between the local date-times.
     * If the second date-time is before the first, the result will be negative.
     * For example, {@code MINUTES.between(dateTime1, dateTime2)} will calculate the difference in minutes.
     * 
     * @param dateTime1  the first date-time, not null
     * @param dateTime2  the second date-time, not null
     * @return the period in terms of this unit, not null
     */
    public Period between(LocalDateTime dateTime1, LocalDateTime dateTime2) {
        return Period.of(periodBetween(dateTime1, dateTime2), this);
    }

    /**
     * Gets the duration of this unit in the ISO calendar system.
     * <p>
     * All units in this class are defined relative to the {@link #SECONDS} unit.
     * The duration of a second is equal to a {@code Duration} of 1 second.
     * <p>
     * All time units in this class are consider to be accurate.
     * Note that this definition ignores leap seconds.
     * 
     * @return the duration of this unit, not null
     */
    @Override
    public Duration getDuration() {
        return duration;
    }

    /**
     * Checks if the duration of the unit is an estimate.
     * <p>
     * All time units in this class are consider to be accurate.
     * Note that this definition ignores leap seconds.
     * 
     * @return true always for these date units
     */
    @Override
    public boolean isDurationEstimated() {
        return false;
    }

    //-----------------------------------------------------------------------
    @Override
    public <R extends CalendricalObject> R addPeriodTo(R calendrical, long period) {
        if (calendrical instanceof LocalDateTime) {
            LocalDateTime dateTime = (LocalDateTime) calendrical;
            switch (this) {
                case NANOS: return (R) dateTime.plusNanos(period);
                case MICROS: return (R) dateTime.plusSeconds(period / (1000000000L * 1000000)).plusNanos((period % 1000000000L) * 1000);
                case MILLIS: return (R) dateTime.plusSeconds(period / (1000000000L * 1000)).plusNanos((period % 1000000000L) * 1000000);
                case SECONDS: return (R) dateTime.plusSeconds(period);
                case MINUTES: return (R) dateTime.plusMinutes(period);
                case HOURS: return (R) dateTime.plusHours(period);
                case HALF_DAYS: return (R) dateTime.plusDays(period / (1000000000L * 2)).plusHours((period % 1000000000L) * 12);
            }
            throw new IllegalStateException("Unreachable");
        }
        if (calendrical instanceof LocalTime) {
            LocalTime time = (LocalTime) calendrical;
            switch (this) {
                case NANOS: return (R) time.plusNanos(period);
                case MICROS: return (R) time.plusNanos((period % MICROS_PER_DAY) * 1000);
                case MILLIS: return (R) time.plusNanos((period % MILLIS_PER_DAY) * 1000000);
                case SECONDS: return (R) time.plusSeconds(period);
                case MINUTES: return (R) time.plusMinutes(period);
                case HOURS: return (R) time.plusHours(period);
                case HALF_DAYS: return (R) time.plusHours((period % 2) * 12);
            }
            throw new IllegalStateException("Unreachable");
        }
        if (calendrical instanceof LocalDate) {
            LocalDate date = (LocalDate) calendrical;
            switch (this) {
                case NANOS: return (R) date.plusDays(period / NANOS_PER_DAY);
                case MICROS: return (R) date.plusDays(period / MICROS_PER_DAY);
                case MILLIS: return (R) date.plusDays(period / MILLIS_PER_DAY);
                case SECONDS: return (R) date.plusDays(period / SECONDS_PER_DAY);
                case MINUTES: return (R) date.plusDays(period / MINUTES_PER_DAY);
                case HOURS: return (R) date.plusDays(period / HOURS_PER_DAY);
                case HALF_DAYS: return (R) date.plusDays(period / 2);
            }
            throw new IllegalStateException("Unreachable");
        }
        throw new CalendricalException("Invalid calendrical type, must be LocalDate, LocalTime or LocalDateTime");
    }

    @Override
    public long periodBetween(CalendricalObject calendrical1, CalendricalObject calendrical2) {
        if (calendrical1 instanceof LocalDateTime && calendrical2 instanceof LocalDateTime) {
            LocalDateTime dateTime1 = (LocalDateTime) calendrical1;
            LocalDateTime dateTime2 = (LocalDateTime) calendrical2;
            return DateTimes.safeAdd(
                    periodBetween(dateTime1.toLocalDate(), dateTime2.toLocalDate()),
                    periodBetween(dateTime1.toLocalTime(), dateTime2.toLocalTime()));
        }
        if (calendrical1 instanceof LocalTime && calendrical2 instanceof LocalTime) {
            LocalTime time1 = (LocalTime) calendrical1;
            LocalTime time2 = (LocalTime) calendrical2;
            switch (this) {
                case NANOS: return time2.toNanoOfDay() - time1.toNanoOfDay();
                case MICROS: return (time2.toNanoOfDay() - time1.toNanoOfDay()) / 1000;
                case MILLIS: return (time2.toNanoOfDay() - time1.toNanoOfDay()) / 1000000;
                case SECONDS: return (time2.toNanoOfDay() - time1.toNanoOfDay()) / NANOS_PER_SECOND;
                case MINUTES: return (time2.toNanoOfDay() - time1.toNanoOfDay()) / NANOS_PER_MINUTE;
                case HOURS: return (time2.toNanoOfDay() - time1.toNanoOfDay()) / NANOS_PER_HOUR;
                case HALF_DAYS: return (time2.toNanoOfDay() - time1.toNanoOfDay()) / (12 * NANOS_PER_HOUR);
            }
            throw new IllegalStateException("Unreachable");
        }
        if (calendrical1 instanceof LocalDate && calendrical2 instanceof LocalDate) {
            LocalDate date1 = (LocalDate) calendrical1;
            LocalDate date2 = (LocalDate) calendrical2;
            long days = DateTimes.safeSubtract(date2.toEpochDay(), date1.toEpochDay());
            switch (this) {
                case NANOS: return DateTimes.safeMultiply(days, NANOS_PER_DAY);
                case MICROS: return DateTimes.safeMultiply(days, MICROS_PER_DAY);
                case MILLIS: return DateTimes.safeMultiply(days, MILLIS_PER_DAY);
                case SECONDS: return DateTimes.safeMultiply(days, SECONDS_PER_DAY);
                case MINUTES: return DateTimes.safeMultiply(days, MINUTES_PER_DAY);
                case HOURS: return DateTimes.safeMultiply(days, HOURS_PER_DAY);
                case HALF_DAYS: return DateTimes.safeMultiply(days, 2);
            }
            throw new IllegalStateException("Unreachable");
        }
        throw new CalendricalException("Invalid calendrical type, must be LocalDate, LocalTime or LocalDateTime");
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
        return getName();
    }

}
