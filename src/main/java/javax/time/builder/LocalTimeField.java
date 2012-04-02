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
package javax.time.builder;

import static javax.time.builder.StandardPeriodUnit.DAYS;
import static javax.time.builder.StandardPeriodUnit.HALF_DAYS;
import static javax.time.builder.StandardPeriodUnit.HOURS;
import static javax.time.builder.StandardPeriodUnit.MICROS;
import static javax.time.builder.StandardPeriodUnit.MILLIS;
import static javax.time.builder.StandardPeriodUnit.MINUTES;
import static javax.time.builder.StandardPeriodUnit.NANOS;
import static javax.time.builder.StandardPeriodUnit.SECONDS;

import javax.time.CalendricalException;
import javax.time.LocalDateTime;
import javax.time.LocalTime;
import javax.time.calendrical.DateTimeRuleRange;

/**
 * A field of time.
 * 
 * @author Stephen Colebourne
 */
public enum LocalTimeField implements TimeField {

    NANO_OF_SECOND("NanoOfSecond", NANOS, SECONDS),
    NANO_OF_DAY("NanoOfDay", NANOS, DAYS),
    MICRO_OF_SECOND("MicroOfSecond", MICROS, SECONDS),
    MICRO_OF_DAY("MicroOfDay", MICROS, DAYS),
    MILLI_OF_SECOND("MilliOfSecond", MILLIS, SECONDS),
    MILLI_OF_DAY("MilliOfDay", MILLIS, DAYS),
    SECOND_OF_MINUTE("SecondOfMinute", SECONDS, MINUTES),
    SECOND_OF_DAY("SecondOfDay", SECONDS, DAYS),
    MINUTE_OF_HOUR("MinuteOfHour", MINUTES, HOURS),
    MINUTE_OF_DAY("MinuteOfDay", MINUTES, DAYS),
    HOUR_OF_AMPM("HourOfAmPm", HOURS, HALF_DAYS),
    CLOCK_HOUR_OF_AMPM("ClockHourOfAmPm", HOURS, HALF_DAYS),
    HOUR_OF_DAY("HourOfDay", HOURS, DAYS),
    CLOCK_HOUR_OF_DAY("ClockHourOfDay", HOURS, DAYS),
    AMPM_OF_DAY("AmPmOfDay", HALF_DAYS, DAYS);

    private final String name;
    private final PeriodUnit baseUnit;
    private final PeriodUnit rangeUnit;
    private final TRules tRules;
    private final DTRules dtRules;

    private LocalTimeField(String name, PeriodUnit baseUnit, PeriodUnit rangeUnit) {
        this.name = name;
        this.baseUnit = baseUnit;
        this.rangeUnit = rangeUnit;
        this.tRules = new TRules(this);
        this.dtRules = new DTRules(this);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public PeriodUnit getBaseUnit() {
        return baseUnit;
    }

    @Override
    public PeriodUnit getRangeUnit() {
        return rangeUnit;
    }

    @Override
    public DateTimeRules<LocalTime> getTimeRules() {
        return tRules;
    }

    @Override
    public DateTimeRules<LocalDateTime> getDateTimeRules() {
        return dtRules;
    }

    @Override
    public String toString() {
        return getName();
    }

    //-------------------------------------------------------------------------
    private static final class TRules implements DateTimeRules<LocalTime> {
        private static final DateTimeRuleRange RANGE_NOS = DateTimeRuleRange.of(0, 999999999);
        private static final DateTimeRuleRange RANGE_NOD = DateTimeRuleRange.of(0, 86400L * 1000000000L - 1);
        private static final DateTimeRuleRange RANGE_MCOS = DateTimeRuleRange.of(0, 999999);
        private static final DateTimeRuleRange RANGE_MCOD = DateTimeRuleRange.of(0, 0, 86400L * 1000000L - 1);
        private static final DateTimeRuleRange RANGE_MLOS = DateTimeRuleRange.of(0, 999);
        private static final DateTimeRuleRange RANGE_MLOD = DateTimeRuleRange.of(0, 0, 86400L * 1000L - 1);
        private static final DateTimeRuleRange RANGE_SOM_MOH = DateTimeRuleRange.of(0, 59);
        private static final DateTimeRuleRange RANGE_SOD = DateTimeRuleRange.of(0, 86400L - 1);
        private static final DateTimeRuleRange RANGE_MOD = DateTimeRuleRange.of(0, 24 * 60 - 1);
        private static final DateTimeRuleRange RANGE_HOD = DateTimeRuleRange.of(0, 23);

        private final LocalTimeField field;
        private TRules(LocalTimeField field) {
            this.field = field;
        }
        @Override
        public DateTimeRuleRange range() {
            switch (field) {
                case NANO_OF_SECOND: return RANGE_NOS;
                case NANO_OF_DAY: return RANGE_NOD;
                case MICRO_OF_SECOND: return RANGE_MCOS;
                case MICRO_OF_DAY: return RANGE_MCOD;
                case MILLI_OF_SECOND: return RANGE_MLOS;
                case MILLI_OF_DAY: return RANGE_MLOD;
                case SECOND_OF_MINUTE: return RANGE_SOM_MOH;
                case SECOND_OF_DAY: return RANGE_SOD;
                case MINUTE_OF_HOUR: return RANGE_SOM_MOH;
                case MINUTE_OF_DAY: return RANGE_MOD;
                case HOUR_OF_DAY: return RANGE_HOD;
            }
            throw new CalendricalException("Unknown field");
        }
        @Override
        public DateTimeRuleRange range(LocalTime date) {
            return range();
        }
        @Override
        public long get(LocalTime time) {
            switch (field) {
                case NANO_OF_DAY: return time.toNanoOfDay();
                case NANO_OF_SECOND: return time.getNanoOfSecond();
                case MICRO_OF_SECOND: return time.getNanoOfSecond() / 1000;
                case MICRO_OF_DAY: return time.toNanoOfDay() / 1000;
                case MILLI_OF_SECOND: return time.getNanoOfSecond() / 1000000;
                case MILLI_OF_DAY: return time.toNanoOfDay() / 1000000;
                case SECOND_OF_MINUTE: return time.getSecondOfMinute();
                case SECOND_OF_DAY: return time.toSecondOfDay();
                case MINUTE_OF_HOUR: return time.getMinuteOfHour();
                case MINUTE_OF_DAY: return time.getHourOfDay() * 60 + time.getMinuteOfHour();
                case HOUR_OF_DAY: return time.getHourOfDay();
            }
            throw new CalendricalException("Unsupported field");
        }
        @Override
        public LocalTime set(LocalTime time, long newValue) {
            if (range(time).isValidValue(newValue) == false) {
                throw new CalendricalException("Invalid value: " + field + " " + newValue);
            }
            switch (field) {
                case NANO_OF_SECOND: return time.withNanoOfSecond((int) newValue);
                case MICRO_OF_SECOND: return time.withNanoOfSecond((int) newValue * 1000);
                case MILLI_OF_SECOND: return time.withNanoOfSecond((int) newValue * 1000000);
                case SECOND_OF_MINUTE: return time.withSecondOfMinute((int) newValue);
                case MINUTE_OF_HOUR: return time.withMinuteOfHour((int) newValue);
                case HOUR_OF_DAY: return time.withHourOfDay((int) newValue);
            }
            throw new CalendricalException("Unsupported field");
        }
        @Override
        public LocalTime setLenient(LocalTime time, long newValue) {
            return null;  // TODO
        }
        @Override
        public LocalTime roll(LocalTime time, long roll) {
            return null;  // TODO
//            DateTimeRuleRange range = range(time);
//            long valueRange = (range.getMaximum() - range.getMinimum()) + 1;
//            long currentValue = get(time);
//            long newValue = roll % valueRange;
//            return addToTime(time, field.getBaseUnit(), newValue - currentValue);
        }
    }

    //-------------------------------------------------------------------------
    private static final class DTRules implements DateTimeRules<LocalDateTime> {
        private final DateTimeRules<LocalTime> rules;
        private DTRules(LocalTimeField field) {
            this.rules = field.getTimeRules();
        }
        @Override
        public DateTimeRuleRange range() {
            return rules.range();
        }
        @Override
        public DateTimeRuleRange range(LocalDateTime dateTime) {
            return rules.range(dateTime.toLocalTime());
        }
        @Override
        public long get(LocalDateTime dateTime) {
            return rules.get(dateTime.toLocalTime());
        }
        @Override
        public LocalDateTime set(LocalDateTime dateTime, long newValue) {
            return dateTime.with(rules.set(dateTime.toLocalTime(), newValue));
        }
        @Override
        public LocalDateTime setLenient(LocalDateTime dateTime, long newValue) {
            return dateTime.with(rules.setLenient(dateTime.toLocalTime(), newValue));
        }
        @Override
        public LocalDateTime roll(LocalDateTime dateTime, long roll) {
            return dateTime.with(rules.roll(dateTime.toLocalTime(), roll));
        }
    }

}
