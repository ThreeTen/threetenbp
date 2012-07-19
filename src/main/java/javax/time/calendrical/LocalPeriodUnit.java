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
import static javax.time.calendrical.LocalDateTimeField.EPOCH_MONTH;

import javax.time.CalendricalException;
import javax.time.DateTimes;
import javax.time.Duration;
import javax.time.LocalDate;
import javax.time.LocalTime;
import javax.time.Period;

/**
 * A standard set of date periods units.
 * <p>
 * These are the basic set of units common across many calendar systems.
 * The calculation part of the units is specific to the ISO calendar system,
 * however the units as concepts may be used with other calendar systems.
 * 
 * <h4>Implementation notes</h4>
 * This is a final, immutable and thread-safe enum.
 */
public enum LocalPeriodUnit implements PeriodUnit {

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
    HALF_DAYS("HalfDays", Duration.ofSeconds(43200)),
    /**
     * Unit that represents the concept of a day.
     * For the ISO calendar system, it is the standard day from midnight to midnight.
     * The estimated duration of a day is {@code 24 Hours}.
     * <p>
     * When used with other calendar systems it must correspond to the day defined by
     * the rising and setting of the Sun on Earth. It is not required that days begin
     * at midnight - when converting between calendar systems, the date should be
     * equivalent at midday.
     */
    DAYS("Days", Duration.ofSeconds(86400)),
    /**
     * Unit that represents the concept of a week.
     * For the ISO calendar system, it is equal to 7 days.
     * <p>
     * When used with other calendar systems it must correspond to an integral number of days.
     */
    WEEKS("Weeks", Duration.ofSeconds(7 * 86400L)),
    /**
     * Unit that represents the concept of a month.
     * For the ISO calendar system, the length of the month varies by month-of-year.
     * The estimated duration of a month is one twelfth of {@code 365.2425 Days}.
     * <p>
     * When used with other calendar systems it must correspond to an integral number of days.
     */
    MONTHS("Months", Duration.ofSeconds(31556952L / 12)),
    /**
     * Unit that represents the concept of a quarter-year.
     * For the ISO calendar system, it is equal to 3 months.
     * The estimated duration of a quarter-year is one quarter of {@code 365.2425 Days}.
     * <p>
     * When used with other calendar systems it must correspond to an integral number of days
     * or months roughly equal to one quarter the length of a year.
     */
    QUARTER_YEARS("QuarterYears", Duration.ofSeconds(31556952L / 4)),
    /**
     * Unit that represents the concept of a half-year.
     * For the ISO calendar system, it is equal to 6 months.
     * The estimated duration of a half-year is half of {@code 365.2425 Days}.
     * <p>
     * When used with other calendar systems it must correspond to an integral number of days
     * or months roughly equal to half the length of a year.
     */
    HALF_YEARS("HalfYears", Duration.ofSeconds(31556952L / 2)),
    /**
     * Unit that represents the concept of a year.
     * For the ISO calendar system, it is equal to 12 months.
     * The estimated duration of a year is {@code 365.2425 Days}.
     * <p>
     * When used with other calendar systems it must correspond to an integral number of days
     * or months roughly equal to a year defined by the passage of the Earth around the Sun.
     */
    YEARS("Years", Duration.ofSeconds(31556952L)),
    /**
     * Unit that represents the concept of a decade.
     * For the ISO calendar system, it is equal to 10 years.
     * <p>
     * When used with other calendar systems it must correspond to an integral number of days
     * and is normally an integral number of years.
     */
    DECADES("Decades", Duration.ofSeconds(31556952L * 10L)),
    /**
     * Unit that represents the concept of a century.
     * For the ISO calendar system, it is equal to 100 years.
     * <p>
     * When used with other calendar systems it must correspond to an integral number of days
     * and is normally an integral number of years.
     */
    CENTURIES("Centuries", Duration.ofSeconds(31556952L * 100L)),
    /**
     * Unit that represents the concept of a millennium.
     * For the ISO calendar system, it is equal to 1000 years.
     * <p>
     * When used with other calendar systems it must correspond to an integral number of days
     * and is normally an integral number of years.
     */
    MILLENNIA("Millennia", Duration.ofSeconds(31556952L * 1000L)),
    /**
     * Unit that represents the concept of an era.
     * The ISO calendar system doesn't have eras thus it is impossible to add
     * an era to a date or date-time.
     * The estimated duration of the era is artificially defined as {@code 1,000,00,000 Years}.
     * <p>
     * When used with other calendar systems there are no restrictions on the unit.
     */
    ERAS("Eras", Duration.ofSeconds(31556952L * 1000000000L)),
    /**
     * Artificial unit that represents the concept of forever.
     * This is primarily used with {@link DateTimeField} to represent unbounded fields
     * such as the year or era.
     * The estimated duration of the era is artificially defined as the largest duration
     * supported by {@code Duration}.
     */
    FOREVER("Forever", Duration.ofSeconds(Long.MAX_VALUE, 999999999));

    private final String name;
    private final Duration duration;

    private LocalPeriodUnit(String name, Duration estimatedDuration) {
        this.name = name;
        this.duration = estimatedDuration;
    }

    //-----------------------------------------------------------------------
    @Override
    public String getName() {
        return name;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the estimated duration of this unit in the ISO calendar system.
     * <p>
     * All of the units in this class have an estimated duration.
     * Days vary due to daylight saving time, while months have different lengths.
     * 
     * @return the estimated duration of this unit, not null
     */
    @Override
    public Duration getDuration() {
        return duration;
    }

    /**
     * Checks if the duration of the unit is an estimate.
     * <p>
     * All time units in this class are considered to be accurate, while all date
     * units in this class are considered to be estimated.
     * <p>
     * This definition ignores leap seconds, but considers that Days vary due to
     * daylight saving time and months have different lengths.
     *
     * @return true if the duration is estimated, false if accurate
     */
    @Override
    public boolean isDurationEstimated() {
        return isDateUnit();
    }

    /**
     * Checks if this unit is a date unit.
     * 
     * @return true if a date unit, false if a time unit
     */
    public boolean isDateUnit() {
        return this.compareTo(DAYS) >= 0;
    }

    //-----------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    @Override
    public <R extends AdjustableDateTime> R doAdd(R dateTime, long periodToAdd) {
        return (R) dateTime.plus(periodToAdd, this);
    }

    //-----------------------------------------------------------------------
    @Override
    public <R extends AdjustableDateTime> Period between(R datetime1, R datetime2) {
        // TODO: better approach needed here
        if (isDateUnit()) {
            LocalDate date1 = datetime1.extract(LocalDate.class);
            LocalDate date2 = datetime2.extract(LocalDate.class);
            if (date1 == null || date2 == null) {
                // No date present, delta is zero
                return Period.of(0, this);
            }
            LocalTime time1 = datetime1.extract(LocalTime.class);
            LocalTime time2 = datetime2.extract(LocalTime.class);
            if (time1 != null || time2 != null) {
                if (time2.isBefore(time1)) {
                    date2 = date2.minusDays(1);
                }
            }
            return Period.of(calculateBetweenForDate(date1, date2), this);
        } else {
            LocalTime time1 = datetime1.extract(LocalTime.class);
            LocalTime time2 = datetime2.extract(LocalTime.class);
            if (time1 == null || time2 == null) {
                throw new CalendricalException("LocalTime not available from " + datetime1 + " or " + datetime2);
            }
            long value = calculateBetweenForTime(time1, time2);
            
            LocalDate date1 = datetime1.extract(LocalDate.class);
            LocalDate date2 = datetime2.extract(LocalDate.class);
            if (date1 != null && date2 != null) {
                 value = DateTimes.safeAdd(value, calculateBetweenForTime(date1, date2));
            }
            return Period.of(value, this);
        }
    }

    private long calculateBetweenForDate(LocalDate date1, LocalDate date2) {
        switch (this) {
            case DAYS: return date2.toEpochDay() - date1.toEpochDay();  // no overflow
            case WEEKS: return DAYS.calculateBetweenForDate(date1, date2) / 7;
            case MONTHS: {
                long months = date2.get(EPOCH_MONTH) - date1.get(EPOCH_MONTH);  // no overflow
                return (date2.getDayOfMonth() <= date1.getDayOfMonth() ? months - 1 : months);
            }
            case QUARTER_YEARS: return MONTHS.calculateBetweenForDate(date1, date2) / 3;
            case HALF_YEARS: return MONTHS.calculateBetweenForDate(date1, date2) / 6;
            case YEARS: {
                long years = ((long) date2.getYear()) - date1.getYear();  // no overflow
                if (date2.getMonthValue() < date1.getMonthValue() || (date2.getMonthValue() == date1.getMonthValue() && date2.getDayOfMonth() <= date1.getDayOfMonth())) {
                    years--;
                }
                return years;
            }
            case DECADES: return YEARS.calculateBetweenForDate(date1, date2) / 10;
            case CENTURIES: return YEARS.calculateBetweenForDate(date1, date2) / 100;
            case MILLENNIA: return YEARS.calculateBetweenForDate(date1, date2) / 1000;
            case ERAS: return 0;
            case FOREVER: return 0;
        }
        throw new IllegalStateException("Unreachable");
    }

    //-----------------------------------------------------------------------
    private long calculateBetweenForTime(LocalDate date1, LocalDate date2) {
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

    private long calculateBetweenForTime(LocalTime time1, LocalTime time2) {
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
