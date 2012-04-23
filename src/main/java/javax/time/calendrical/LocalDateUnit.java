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

import javax.time.CalendricalException;
import javax.time.DateTimes;
import javax.time.Duration;
import javax.time.LocalDate;
import javax.time.LocalDateTime;
import javax.time.LocalTime;
import javax.time.Period;

/**
 * A standard set of time periods units not tied to any specific calendar system.
 * <p>
 * These are the basic set of units common across many calendar systems.
 * <p>
 * This is a final, immutable and thread-safe enum.
 */
public enum LocalDateUnit implements PeriodUnit {

    /**
     * Unit that represents the concept of a day.
     * The exact meaning of this unit is chronology specific, however it must correspond
     * to the day defined by the rising and setting of the Sun on Earth.
     * It is not required that days begin at midnight - when converting between calendar
     * systems, the date should be equivalent at midday.
     * All supplied chronologies use a definition, ignoring time-zones, that is equal to 24 hours.
     */
    DAYS("Days", Duration.ofSeconds(86400)),
    /**
     * Unit that represents the concept of a week.
     * The exact meaning of this unit is chronology specific, however it must be
     * an integral number of days.
     * A week is typically 7 days, however some calendar systems have other week lengths.
     */
    WEEKS("Weeks", Duration.ofSeconds(7 * 86400L)),
    /**
     * Unit that represents the concept of a month.
     * The exact meaning of this unit is chronology specific, however it must be
     * an integral number of days.
     */
    MONTHS("Months", Duration.ofSeconds(31556952L / 12)),
    /**
     * Unit that represents the concept of a quarter-year.
     * The exact meaning of this unit is chronology specific, although it should generally
     * be about one quarter the length of a year. It must be an integral number of days.
     */
    QUARTER_YEARS("QuarterYears", Duration.ofSeconds(31556952L / 4)),
    /**
     * Unit that represents the concept of a half-year.
     * The exact meaning of this unit is chronology specific, although it should generally
     * be about half the length of a year. It must be an integral number of days.
     */
    HALF_YEARS("HalfYears", Duration.ofSeconds(31556952L / 2)),
    /**
     * Unit that represents the concept of a year.
     * The exact meaning of this unit is chronology specific, however it must be
     * an integral number of days and should relate to some degree to the passage
     * of the Earth around the Sun.
     */
    YEARS("Years", Duration.ofSeconds(31556952L)),
    /**
     * Unit that represents the concept of a decade.
     * The exact meaning of this unit is chronology specific, however it must be
     * an integral number of days and is normally an integral number of years.
     * All supplied chronologies use a definition that is equal to 10 years.
     */
    DECADES("Decades", Duration.ofSeconds(31556952L * 10L)),
    /**
     * Unit that represents the concept of a century.
     * The exact meaning of this unit is chronology specific, however it must be
     * an integral number of days and is normally an integral number of years.
     * All supplied chronologies use a definition that is equal to 100 years.
     */
    CENTURIES("Centuries", Duration.ofSeconds(31556952L * 100L)),
    /**
     * Unit that represents the concept of a millenium.
     * The exact meaning of this unit is chronology specific, however it must be
     * an integral number of days and is normally an integral number of years.
     * All supplied chronologies use a definition that is equal to 1000 years.
     */
    MILLENIA("Millenia", Duration.ofSeconds(31556952L * 1000L)),
    /**
     * Unit that represents the concept of an era.
     * The exact meaning of this unit is chronology specific.
     * All supplied chronologies use a definition that is equal to 1000 years.
     */
    ERAS("Eras", Duration.ofSeconds(31556952L * 1000000000L)),
    /**
     * Unit that represents the concept of forever.
     * This is primarily used with {@link DateTimeField} to represent unbounded fields
     * such as the year or era.
     */
    FOREVER("Forever", Duration.ofSeconds(Long.MAX_VALUE, 999999999));

    private final String name;
    private final Duration estimatedDuration;
    private final Rules rules;

    private LocalDateUnit(String name, Duration estimatedDuration) {
        this.name = name;
        this.estimatedDuration = estimatedDuration;
        this.rules = new DRules(this);
    }

    //-----------------------------------------------------------------------
    @Override
    public String getName() {
        return name;
    }

    @Override
    public Rules getRules() {
        return rules;
    }

    public Period between(LocalDate date1, LocalDate date2) {
        return Period.of(getRules().getPeriodBetweenDates(date1, date2), this);
    }

    public Period between(LocalDateTime dateTime1, LocalDateTime dateTime2) {
        return Period.of(getRules().getPeriodBetweenDateTimes(dateTime1, dateTime2), this);
    }

    public Duration getEstimatedDuration() {
        return estimatedDuration;  // ISO specific, OK if not in interface
    }

    @Override
    public String toString() {
        return getName();
    }

    //-------------------------------------------------------------------------
    /**
     * Date rules for the field.
     */
    private static final class DRules implements Rules {
        private final LocalDateUnit unit;
        private DRules(LocalDateUnit unit) {
            this.unit = unit;
        }
        //-----------------------------------------------------------------------
        @Override
        public LocalDate addToDate(LocalDate date, long amount) {
            switch (unit) {
                case DAYS: return date.plusDays(amount);
                case WEEKS: return date.plusWeeks(amount);
                case MONTHS: return date.plusMonths(amount);
                case QUARTER_YEARS: return date.plusMonths(DateTimes.safeMultiply(amount, 3));
                case HALF_YEARS: return date.plusMonths(DateTimes.safeMultiply(amount, 6));
                case YEARS: return date.plusYears(amount);
                case DECADES: return date.plusYears(DateTimes.safeMultiply(amount, 10));
                case CENTURIES: return date.plusYears(DateTimes.safeMultiply(amount, 100));
                case MILLENIA: return date.plusYears(DateTimes.safeMultiply(amount, 1000));
                case ERAS: return date;  // TODO
                case FOREVER: return date;  // TODO: move elsewhere (make semi-private?)
            }
            throw new CalendricalException("Unknown unit");
        }
        @Override
        public LocalTime addToTime(LocalTime time, long amount) {
            return time;  // TODO: should we allow this? doesn't really cause any harm AFAICT
        }
        @Override
        public LocalDateTime addToDateTime(LocalDateTime dateTime, long amount) {
            return dateTime.with(addToDate(dateTime.toLocalDate(), amount));
        }
        //-----------------------------------------------------------------------
        @Override
        public long getPeriodBetweenDates(LocalDate date1, LocalDate date2) {
            switch (unit) {
                case DAYS: return DateTimes.safeSubtract(date2.toEpochDay(), date1.toEpochDay());
                case WEEKS: return DAYS.getRules().getPeriodBetweenDates(date1, date2) / 7;
                case MONTHS: return 0;  // TODO: case for epoch months
                case QUARTER_YEARS: return MONTHS.getRules().getPeriodBetweenDates(date1, date2) / 3;
                case HALF_YEARS: return MONTHS.getRules().getPeriodBetweenDates(date1, date2) / 6;
                case YEARS: {
                    // TODO: handle month/day - this doesn't calculate right when negative
                    return DateTimes.safeSubtract(date2.minusDays(date1.getDayOfYear() - 1).getYear(), date1.getYear());
                }
                case DECADES: return YEARS.getRules().getPeriodBetweenDates(date1, date2) / 10;
                case CENTURIES: return YEARS.getRules().getPeriodBetweenDates(date1, date2) / 100;
                case MILLENIA: return YEARS.getRules().getPeriodBetweenDates(date1, date2) / 1000;
                case ERAS: return 0;  // TODO
                case FOREVER: return 0;  // TODO: move elsewhere (make semi-private?)
            }
            throw new CalendricalException("Unknown unit");
        }
        @Override
        public long getPeriodBetweenTimes(LocalTime time1, LocalTime time2) {
            return 0;  // TODO: should we allow this? doesn't really cause any harm AFAICT
        }
        @Override
        public long getPeriodBetweenDateTimes(LocalDateTime dateTime1, LocalDateTime dateTime2) {
            return 0;  // TODO
        }
    }

}
