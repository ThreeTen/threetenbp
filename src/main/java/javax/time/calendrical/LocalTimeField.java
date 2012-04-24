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

import static javax.time.calendrical.LocalDateUnit.DAYS;
import static javax.time.calendrical.LocalTimeUnit.HALF_DAYS;
import static javax.time.calendrical.LocalTimeUnit.HOURS;
import static javax.time.calendrical.LocalTimeUnit.MICROS;
import static javax.time.calendrical.LocalTimeUnit.MILLIS;
import static javax.time.calendrical.LocalTimeUnit.MINUTES;
import static javax.time.calendrical.LocalTimeUnit.NANOS;
import static javax.time.calendrical.LocalTimeUnit.SECONDS;

import javax.time.CalendricalException;
import javax.time.DateTimes;
import javax.time.LocalDateTime;
import javax.time.LocalTime;

/**
 * A standard set of {@code LocalTime} fields.
 * <p>
 * This set of fields provide framework-level access to manipulate a {@code LocalTime}.
 * 
 * <h4>Implementation notes</h4>
 * This is a final, immutable and thread-safe enum.
 * 
 * @see LocalDateField
 */
public enum LocalTimeField implements TimeField {

    /**
     * The nano-of-second.
     * This counts the nanosecond within the second, from 0 to 999,999,999.
     */
    NANO_OF_SECOND("NanoOfSecond", NANOS, SECONDS, DateTimeValueRange.of(0, 999999999)),
    /**
     * The nano-of-day.
     * This counts the nanosecond within the day, from 0 to (24 * 60 * 60 * 1,000,000,000) - 1.
     */
    NANO_OF_DAY("NanoOfDay", NANOS, DAYS, DateTimeValueRange.of(0, 86400L * 1000000000L - 1)),
    /**
     * The micro-of-second.
     * This counts the microsecond within the second, from 0 to 999,999.
     */
    MICRO_OF_SECOND("MicroOfSecond", MICROS, SECONDS, DateTimeValueRange.of(0, 999999)),
    /**
     * The micro-of-day.
     * This counts the microsecond within the day, from 0 to (24 * 60 * 60 * 1,000,000) - 1.
     */
    MICRO_OF_DAY("MicroOfDay", MICROS, DAYS, DateTimeValueRange.of(0, 86400L * 1000000L - 1)),
    /**
     * The milli-of-second.
     * This counts the millisecond within the second, from 0 to 999.
     */
    MILLI_OF_SECOND("MilliOfSecond", MILLIS, SECONDS, DateTimeValueRange.of(0, 999)),
    /**
     * The milli-of-day.
     * This counts the millisecond within the day, from 0 to (24 * 60 * 60 * 1,000) - 1.
     */
    MILLI_OF_DAY("MilliOfDay", MILLIS, DAYS, DateTimeValueRange.of(0, 86400L * 1000L - 1)),
    /**
     * The second-of-minute.
     * This counts the second within the minute, from 0 to 59.
     */
    SECOND_OF_MINUTE("SecondOfMinute", SECONDS, MINUTES, DateTimeValueRange.of(0, 59)),
    /**
     * The second-of-day.
     * This counts the second within the day, from 0 to (24 * 60 * 60) - 1.
     */
    SECOND_OF_DAY("SecondOfDay", SECONDS, DAYS, DateTimeValueRange.of(0, 86400L - 1)),
    /**
     * The minute-of-hour.
     * This counts the minute within the hour, from 0 to 59.
     */
    MINUTE_OF_HOUR("MinuteOfHour", MINUTES, HOURS, DateTimeValueRange.of(0, 59)),
    /**
     * The minute-of-day.
     * This counts the minute within the day, from 0 to (24 * 60) - 1.
     */
    MINUTE_OF_DAY("MinuteOfDay", MINUTES, DAYS, DateTimeValueRange.of(0, (24 * 60) - 1)),
    /**
     * The hour-of-am-pm.
     * This counts the hour within the AM/PM, from 0 to 11.
     */
    HOUR_OF_AMPM("HourOfAmPm", HOURS, HALF_DAYS, DateTimeValueRange.of(0, 11)),
    /**
     * The clock-hour-of-am-pm.
     * This counts the hour within the AM/PM, from 1 to 12.
     */
    CLOCK_HOUR_OF_AMPM("ClockHourOfAmPm", HOURS, HALF_DAYS, DateTimeValueRange.of(1, 12)),
    /**
     * The hour-of-day.
     * This counts the hour within the day, from 0 to 23.
     */
    HOUR_OF_DAY("HourOfDay", HOURS, DAYS, DateTimeValueRange.of(0, 23)),
    /**
     * The clock-hour-of-day.
     * This counts the hour within the AM/PM, from 1 to 24.
     */
    CLOCK_HOUR_OF_DAY("ClockHourOfDay", HOURS, DAYS, DateTimeValueRange.of(1, 24)),
    /**
     * The am-pm-of-day.
     * This counts the AM/PM within the day, from 0 (AM) to 1 (PM).
     */
    AMPM_OF_DAY("AmPmOfDay", HALF_DAYS, DAYS, DateTimeValueRange.of(0, 1)),
    ;

    private final String name;
    private final PeriodUnit baseUnit;
    private final PeriodUnit rangeUnit;
    private final Rules<LocalTime> tRules;
    private final Rules<LocalDateTime> dtRules;
    private final DateTimeValueRange range;

    private LocalTimeField(String name, PeriodUnit baseUnit, PeriodUnit rangeUnit, DateTimeValueRange range) {
        this.name = name;
        this.baseUnit = baseUnit;
        this.rangeUnit = rangeUnit;
        this.tRules = new TRules(this);
        this.dtRules = DateTimes.rulesForTime(this.tRules);
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
    public Rules<LocalTime> getTimeRules() {
        return tRules;
    }

    @Override
    public Rules<LocalDateTime> getDateTimeRules() {
        return dtRules;
    }

    @Override
    public DateTimeValueRange getValueRange() {
        return range;
    }

    @Override
    public long getValueFrom(CalendricalObject calendrical) {
        LocalTime time = calendrical.extract(LocalTime.class);
        if (time != null) {
            return getTimeRules().get(time);
        }
        DateTimeBuilder builder = calendrical.extract(DateTimeBuilder.class);
        if (builder.containsFieldValue(this)) {
            return builder.getFieldValue(this);
        }
        throw new CalendricalException("Unable to obtain " + getName() + " from calendrical: " + calendrical.getClass());
    }

    @Override
    public int compare(CalendricalObject calendrical1, CalendricalObject calendrical2) {
        return DateTimes.safeCompare(getValueFrom(calendrical1), getValueFrom(calendrical2));
    }

    //-----------------------------------------------------------------------
    /**
     * Checks that the specified value is valid for this field.
     * <p>
     * This validates that the value is within the outer range of valid values
     * returned by {@link #getValueRange()}.
     * 
     * @param value  the value to check
     * @return the value that was passed in
     */
    public long checkValidValue(long value) {  // JAVA8 default method on interface
        return getValueRange().checkValidValue(value, this);
    }

    /**
     * Checks that the specified value is valid and fits in an {@code int}.
     * <p>
     * This validates that the value is within the outer range of valid values
     * returned by {@link #getValueRange()}.
     * It also checks that all valid values are within the bounds of an {@code int}.
     * 
     * @param value  the value to check
     * @return the value that was passed in
     */
    public int checkValidIntValue(long value) {  // JAVA8 default method on interface
        return getValueRange().checkValidIntValue(value, this);
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
        return getName();
    }

    //-------------------------------------------------------------------------
    /**
     * Time rules for the field.
     */
    private static final class TRules implements Rules<LocalTime> {
        private final LocalTimeField field;
        private TRules(LocalTimeField field) {
            this.field = field;
        }
        @Override
        public DateTimeValueRange range(LocalTime date) {
            return field.getValueRange();
        }
        @Override
        public long get(LocalTime time) {
            switch (field) {
                case NANO_OF_SECOND: return time.getNanoOfSecond();
                case NANO_OF_DAY: return time.toNanoOfDay();
                case MICRO_OF_SECOND: return time.getNanoOfSecond() / 1000;
                case MICRO_OF_DAY: return time.toNanoOfDay() / 1000;
                case MILLI_OF_SECOND: return time.getNanoOfSecond() / 1000000;
                case MILLI_OF_DAY: return time.toNanoOfDay() / 1000000;
                case SECOND_OF_MINUTE: return time.getSecondOfMinute();
                case SECOND_OF_DAY: return time.toSecondOfDay();
                case MINUTE_OF_HOUR: return time.getMinuteOfHour();
                case MINUTE_OF_DAY: return time.getHourOfDay() * 60 + time.getMinuteOfHour();
                case HOUR_OF_AMPM: return time.getHourOfDay() % 12;
                case HOUR_OF_DAY: return time.getHourOfDay();
                case AMPM_OF_DAY: return time.getHourOfDay() / 12;
            }
            throw new IllegalStateException("Unreachable");
        }
        @Override
        public LocalTime set(LocalTime time, long newValue) {
            if (range(time).isValidValue(newValue) == false) {
                throw new CalendricalException("Invalid value: " + field + " " + newValue);
            }
            switch (field) {
                case NANO_OF_SECOND: return time.withNanoOfSecond((int) newValue);
                case NANO_OF_DAY: return LocalTime.ofNanoOfDay(newValue);
                case MICRO_OF_SECOND: return time.withNanoOfSecond((int) newValue * 1000);
                case MICRO_OF_DAY: return time.plusNanos((newValue - time.toNanoOfDay() / 1000) * 1000);
                case MILLI_OF_SECOND: return time.withNanoOfSecond((int) newValue * 1000000);
                case MILLI_OF_DAY: return time.plusNanos((newValue - time.toNanoOfDay() / 1000000) * 1000000);
                case SECOND_OF_MINUTE: return time.withSecondOfMinute((int) newValue);
                case SECOND_OF_DAY: return time.plusSeconds(newValue - time.toSecondOfDay());
                case MINUTE_OF_HOUR: return time.withMinuteOfHour((int) newValue);
                case MINUTE_OF_DAY: return time.plusMinutes(newValue - (time.getHourOfDay() * 60 + time.getMinuteOfHour()));
                case HOUR_OF_AMPM: return time.plusHours(newValue - (time.getHourOfDay() % 12));
                case HOUR_OF_DAY: return time.withHourOfDay((int) newValue);
                case AMPM_OF_DAY: return time.plusHours((newValue - (time.getHourOfDay() / 12)) * 12);
            }
            throw new IllegalStateException("Unreachable");
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
        @Override
        public boolean resolve(DateTimeBuilder builder, long value) {
            return false;  // resolve implemented in builder
        }
    }

}
