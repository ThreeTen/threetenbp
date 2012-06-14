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
import static javax.time.DateTimes.MICROS_PER_DAY;
import static javax.time.DateTimes.MILLIS_PER_DAY;
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
     * All time units in this class are considered to be accurate.
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
    public <R extends CalendricalObject> R roll(R calendrical, long amount) {
        // Delegate to LocalTimeField for the corresponding field
        switch (this) {
            case NANOS: return LocalTimeField.NANO_OF_SECOND.roll(calendrical, amount);
            case MICROS: return LocalTimeField.MICRO_OF_SECOND.roll(calendrical, amount);
            case MILLIS: return LocalTimeField.MILLI_OF_SECOND.roll(calendrical, amount);
            case SECONDS: return LocalTimeField.SECOND_OF_MINUTE.roll(calendrical, amount);
            case MINUTES: return LocalTimeField.MINUTE_OF_HOUR.roll(calendrical, amount);
            case HOURS: return LocalTimeField.HOUR_OF_DAY.roll(calendrical, amount);
            case HALF_DAYS: return LocalTimeField.HOUR_OF_DAY.roll(calendrical, amount / 2);
            default:
                throw new IllegalStateException("Unreachable");
        }
    }

    //-----------------------------------------------------------------------
    @Override
    public <R extends CalendricalObject> Period between(R datetime1, R datetime2) {
        LocalTime time1 = datetime1.extract(LocalTime.class);
        LocalTime time2 = datetime2.extract(LocalTime.class);
        if (time1 == null || time2 == null) {
            throw new CalendricalException("LocalTime not available from " + datetime1 + " or " + datetime2);
        }
        long value = calculateBetween(time1, time2);
        
        LocalDate date1 = datetime1.extract(LocalDate.class);
        LocalDate date2 = datetime2.extract(LocalDate.class);
        if (date1 != null && date2 != null) {
             value = DateTimes.safeAdd(value, calculateBetween(date1, date2));
        }
        return Period.of(value, this);
    }

    //-----------------------------------------------------------------------
    private long calculateBetween(LocalDate date1, LocalDate date2) {
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

    private long calculateBetween(LocalTime time1, LocalTime time2) {
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

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
        return getName();
    }

}
