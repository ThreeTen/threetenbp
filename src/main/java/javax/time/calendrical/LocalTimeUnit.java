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
public enum LocalTimeUnit implements PeriodUnit {

    /**
     * Unit that represents the concept of a nanosecond.
     * This unit is the smallest supported unit of time.
     * It is usually equal to the 1,000,000,000th part of the second unit, however this
     * definition is chronology specific.
     */
    NANOS("Nanos", Duration.ofNanos(1)),
    /**
     * Unit that represents the concept of a microsecond.
     * It is usually equal to the 1,000,000th part of the second unit, however this
     * definition is chronology specific.
     */
    MICROS("Micros", Duration.ofNanos(1000)),
    /**
     * Unit that represents the concept of a millisecond.
     * It is usually equal to the 1000th part of the second unit, however this
     * definition is chronology specific.
     */
    MILLIS("Millis", Duration.ofMillis(1000000)),
    /**
     * Unit that represents the concept of a second.
     * The exact meaning of this unit is chronology specific.
     * All supplied chronologies use a definition that is equal to a second in the
     * SI system of units except around a leap-second.
     */
    SECONDS("Seconds", Duration.ofSeconds(1)),
    /**
     * Unit that represents the concept of a minute.
     * The exact meaning of this unit is chronology specific.
     * All supplied chronologies use a definition that is equal to 60 seconds.
     */
    MINUTES("Minutes", Duration.ofSeconds(60)),
    /**
     * Unit that represents the concept of an hour.
     * The exact meaning of this unit is chronology specific.
     * All supplied chronologies use a definition that is equal to 60 minutes.
     */
    HOURS("Hours", Duration.ofSeconds(3600)),
    /**
     * Unit that represents the concept of half a day, as used in AM/PM.
     * The exact meaning of this unit is chronology specific.
     * All supplied chronologies use a definition that is equal to 12 hours.
     */
    HALF_DAYS("HalfDays", Duration.ofSeconds(43200));

    private final String name;
    private final Duration duration;
    private final Rules rules;

    private LocalTimeUnit(String name, Duration duration) {
        this.name = name;
        this.duration = duration;
        this.rules = new TRules(this);
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

    public Duration getDuration() {
        return duration;  // ISO specific, OK if not in interface
    }

    public Period between(LocalTime time1, LocalTime time2) {
        return Period.of(getRules().getPeriodBetweenTimes(time1, time2), this);
    }

    public Period between(LocalDateTime dateTime1, LocalDateTime dateTime2) {
        return Period.of(getRules().getPeriodBetweenDateTimes(dateTime1, dateTime2), this);
    }

    @Override
    public String toString() {
        return getName();
    }

    //-------------------------------------------------------------------------
    /**
     * Date rules for the field.
     */
    private static final class TRules implements Rules {
        private final LocalTimeUnit unit;
        private TRules(LocalTimeUnit unit) {
            this.unit = unit;
        }
        //-----------------------------------------------------------------------
        @Override
        public LocalDate addToDate(LocalDate date, long amount) {
            return date;  // TODO: does add 25 hours add to the date? or exception
        }
        @Override
        public LocalTime addToTime(LocalTime time, long amount) {
            switch (unit) {
                case NANOS: return time.plusNanos(amount);
                case MICROS: return time.plusNanos((amount % (24 * 60 * 60 * 1000000L)) * 1000);
                case MILLIS: return time.plusNanos((amount % (24 * 60 * 60 * 1000L)) * 1000000);
                case SECONDS: return time.plusSeconds(amount);
                case MINUTES: return time.plusMinutes(amount);
                case HOURS: return time.plusHours(amount);
                case HALF_DAYS: return time.plusHours((amount % 2) * 12);
            }
            throw new CalendricalException("Unknown unit");
        }
        @Override
        public LocalDateTime addToDateTime(LocalDateTime dateTime, long amount) {
            switch (unit) {
                case NANOS: return dateTime.plusNanos(amount);
                case MICROS: return dateTime.plusSeconds(amount / (1000000000L * 1000000)).plusNanos((amount % 1000000000L) * 1000);
                case MILLIS: return dateTime.plusSeconds(amount / (1000000000L * 1000)).plusNanos((amount % 1000000000L) * 1000000);
                case SECONDS: return dateTime.plusSeconds(amount);
                case MINUTES: return dateTime.plusMinutes(amount);
                case HOURS: return dateTime.plusHours(amount);
                case HALF_DAYS: return dateTime.plusDays(amount / (1000000000L * 2)).plusHours((amount % 1000000000L) * 12);
            }
            throw new CalendricalException("Unknown unit");
        }
        //-----------------------------------------------------------------------
        @Override
        public long getPeriodBetweenDates(LocalDate date1, LocalDate date2) {
            return 0;  // TODO
        }
        @Override
        public long getPeriodBetweenTimes(LocalTime time1, LocalTime time2) {
            switch (unit) {
                case NANOS: return time2.toNanoOfDay() - time1.toNanoOfDay();
                case MICROS: return (time2.toNanoOfDay() - time1.toNanoOfDay()) / 1000;
                case MILLIS: return (time2.toNanoOfDay() - time1.toNanoOfDay()) / 1000000;
                case SECONDS: return (time2.toNanoOfDay() - time1.toNanoOfDay()) / 1000000000L;
                case MINUTES: return (time2.toNanoOfDay() - time1.toNanoOfDay()) / 60 * 1000000000L;
                case HOURS: return (time2.toNanoOfDay() - time1.toNanoOfDay()) / 60 * 60 * 1000000000L;
                case HALF_DAYS: return (time2.toNanoOfDay() - time1.toNanoOfDay()) / 12 * 60 * 60 * 1000000000L;
            }
            throw new CalendricalException("Unknown unit");
        }
        @Override
        public long getPeriodBetweenDateTimes(LocalDateTime dateTime1, LocalDateTime dateTime2) {
            return 0;  // TODO
        }
    }

}
