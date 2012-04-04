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

import static javax.time.builder.LocalDateUnit.DAYS;
import static javax.time.builder.LocalTimeUnit.HALF_DAYS;
import static javax.time.builder.LocalTimeUnit.HOURS;
import static javax.time.builder.LocalTimeUnit.MICROS;
import static javax.time.builder.LocalTimeUnit.MILLIS;
import static javax.time.builder.LocalTimeUnit.MINUTES;
import static javax.time.builder.LocalTimeUnit.NANOS;
import static javax.time.builder.LocalTimeUnit.SECONDS;

import javax.time.CalendricalException;
import javax.time.LocalDateTime;
import javax.time.LocalTime;
import javax.time.calendrical.DateTimeRuleRange;

/**
 * A standard set of {@code LocalTime} fields.
 * <p>
 * This set of fields provide framework-level access to manipulate a {@code LocalTime}.
 * 
 * @see LocalDateField
 */
public enum LocalTimeField implements TimeField {

    NANO_OF_SECOND("NanoOfSecond", NANOS, SECONDS, DateTimeRuleRange.of(0, 999999999)),
    NANO_OF_DAY("NanoOfDay", NANOS, DAYS, DateTimeRuleRange.of(0, 86400L * 1000000000L - 1)),
    MICRO_OF_SECOND("MicroOfSecond", MICROS, SECONDS, DateTimeRuleRange.of(0, 999999)),
    MICRO_OF_DAY("MicroOfDay", MICROS, DAYS, DateTimeRuleRange.of(0, 0, 86400L * 1000000L - 1)),
    MILLI_OF_SECOND("MilliOfSecond", MILLIS, SECONDS, DateTimeRuleRange.of(0, 999)),
    MILLI_OF_DAY("MilliOfDay", MILLIS, DAYS, DateTimeRuleRange.of(0, 0, 86400L * 1000L - 1)),
    SECOND_OF_MINUTE("SecondOfMinute", SECONDS, MINUTES, DateTimeRuleRange.of(0, 59)),
    SECOND_OF_DAY("SecondOfDay", SECONDS, DAYS, DateTimeRuleRange.of(0, 86400L - 1)),
    MINUTE_OF_HOUR("MinuteOfHour", MINUTES, HOURS, DateTimeRuleRange.of(0, 59)),
    MINUTE_OF_DAY("MinuteOfDay", MINUTES, DAYS, DateTimeRuleRange.of(0, (24 * 60) - 1)),
    HOUR_OF_AMPM("HourOfAmPm", HOURS, HALF_DAYS, DateTimeRuleRange.of(0, 11)),
    CLOCK_HOUR_OF_AMPM("ClockHourOfAmPm", HOURS, HALF_DAYS, DateTimeRuleRange.of(1, 12)),
    HOUR_OF_DAY("HourOfDay", HOURS, DAYS, DateTimeRuleRange.of(0, 23)),
    CLOCK_HOUR_OF_DAY("ClockHourOfDay", HOURS, DAYS, DateTimeRuleRange.of(1, 24)),
    AMPM_OF_DAY("AmPmOfDay", HALF_DAYS, DAYS, DateTimeRuleRange.of(0, 1));

    private final String name;
    private final PeriodUnit baseUnit;
    private final PeriodUnit rangeUnit;
    private final TRules tRules;
    private final DTRules dtRules;
    private final DateTimeRuleRange range;

    private LocalTimeField(String name, PeriodUnit baseUnit, PeriodUnit rangeUnit, DateTimeRuleRange range) {
        this.name = name;
        this.baseUnit = baseUnit;
        this.rangeUnit = rangeUnit;
        this.tRules = new TRules(this);
        this.dtRules = new DTRules(this);
        this.range = range;
    }

    //-----------------------------------------------------------------------
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
    public DateTimeRuleRange getValueRange() {
        return range;
    }

    @Override
    public long getValueFrom(CalendricalObject calendrical) {
        return getTimeRules().get(calendrical.extract(LocalTime.class));
    }

    @Override
    public String toString() {
        return getName();
    }

    //-------------------------------------------------------------------------
    /**
     * Time rules for the field.
     */
    private static final class TRules implements DateTimeRules<LocalTime> {
        private final LocalTimeField field;
        private TRules(LocalTimeField field) {
            this.field = field;
        }
        @Override
        public DateTimeRuleRange range(LocalTime date) {
            return field.getValueRange();
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
    /**
     * Date-time rules for the field.
     */
    private static final class DTRules implements DateTimeRules<LocalDateTime> {
        private final DateTimeRules<LocalTime> rules;
        private DTRules(TimeField field) {
            this.rules = field.getTimeRules();
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
