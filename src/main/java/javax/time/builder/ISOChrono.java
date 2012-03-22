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

import javax.time.CalendricalException;
import javax.time.Duration;
import javax.time.LocalDate;
import javax.time.LocalDateTime;
import javax.time.LocalTime;
import javax.time.MathUtils;
import javax.time.calendrical.DateTimeRuleRange;

/**
 * The ISO-8601 calendar system.
 * 
 * @author Stephen Colebourne
 */
public class ISOChrono implements Chrono, PeriodRules {

    /**
     * Singleton instance.
     */
    public static final ISOChrono INSTANCE = new ISOChrono();
    /**
     * The minimum permitted year.
     */
    private static final int MIN_YEAR = -999999998;
    /**
     * The maximum permitted year.
     */
    private static final int MAX_YEAR = 999999999;
    /**
     * The minimum permitted epoch-month.
     */
    private static final long MIN_EPOCH_MONTH = (MIN_YEAR - 1970L) * 12L;
    /**
     * The maximum permitted epoch-month.
     */
    private static final long MAX_EPOCH_MONTH = (MAX_YEAR - 1970L) * 12L - 1L;
    /**
     * The minimum permitted epoch-day.
     */
    private static final long MIN_EPOCH_DAY = 0;
    /**
     * The maximum permitted epoch-day.
     */
    private static final long MAX_EPOCH_DAY = 0;

    @Override
    public String getName() {
        return "ISO";
    }

    //-----------------------------------------------------------------------
    @Override
    public DateTimeRuleRange getDateValueRange(DateTimeField field, LocalDate date) {
        if (field instanceof StandardDateTimeField) {
            return ISODateRules.INSTANCE.range(field, date);
        }
        return field.implementationDateRules(this).range(field, date);
    }

    @Override
    public DateTimeRuleRange getTimeValueRange(DateTimeField field, LocalTime time) {
        if (field instanceof StandardDateTimeField) {
            return ISOTimeRules.INSTANCE.range(field, time);
        }
        return field.implementationTimeRules(this).range(field, time);
    }

    @Override
    public DateTimeRuleRange getDateTimeValueRange(DateTimeField field, LocalDateTime dateTime) {
        if (field instanceof StandardDateTimeField) {
            StandardDateTimeField std = (StandardDateTimeField) field;
            if (std.isDateField()) {
                return getDateValueRange(field, dateTime != null ? dateTime.toLocalDate() : null);
            } else {
                return getTimeValueRange(field, dateTime != null ? dateTime.toLocalTime() : null);
            }
        }
        return field.implementationDateTimeRules(this).range(field, dateTime);
    }

    //-----------------------------------------------------------------------
    @Override
    public long getDateValue(LocalDate date, DateTimeField field) {
        if (field instanceof StandardDateTimeField) {
            return ISODateRules.INSTANCE.get(date, field);
        }
        return field.implementationDateRules(this).get(date, field);
    }

    //-----------------------------------------------------------------------
    @Override
    public long getTimeValue(LocalTime time, DateTimeField field) {
        if (field instanceof StandardDateTimeField) {
            switch ((StandardDateTimeField) field) {
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
        return field.implementationTimeRules(this).get(time, field);
    }

    //-----------------------------------------------------------------------
    @Override
    public long getDateTimeValue(LocalDateTime dateTime, DateTimeField field) {
        if (field instanceof StandardDateTimeField) {
            StandardDateTimeField std = (StandardDateTimeField) field;
            if (std.isDateField()) {
                return getDateValue(dateTime.toLocalDate(), field);
            } else {
                return getTimeValue(dateTime.toLocalTime(), field);
            }
        }
        return field.implementationDateTimeRules(this).get(dateTime, field);
    }

    //-----------------------------------------------------------------------
    @Override
    public LocalDate setDate(LocalDate date, DateTimeField field, long newValue) {
        if (field instanceof StandardDateTimeField) {
            return ISODateRules.INSTANCE.set(date, field, newValue);
        }
        return field.implementationDateRules(this).set(date, field, newValue);
    }

    @Override
    public LocalTime setTime(LocalTime time, DateTimeField field, long newValue) {
        if (field instanceof StandardDateTimeField) {
            return ISOTimeRules.INSTANCE.set(time, field, newValue);
        }
        return field.implementationTimeRules(this).set(time, field, newValue);
    }

    @Override
    public LocalDateTime setDateTime(LocalDateTime dateTime, DateTimeField field, long newValue) {
        if (field instanceof StandardDateTimeField) {
            StandardDateTimeField std = (StandardDateTimeField) field;
            if (std.isDateField()) {
                return dateTime.with(setDate(dateTime.toLocalDate(), field, newValue));
            } else {
                return dateTime.with(setTime(dateTime.toLocalTime(), field, newValue));
            }
        }
        return field.implementationDateTimeRules(this).set(dateTime, field, newValue);
    }

    //-----------------------------------------------------------------------
    @Override
    public LocalDate setDateLenient(LocalDate date, DateTimeField field, long newValue) {
        if (field instanceof StandardDateTimeField) {
            return ISODateRules.INSTANCE.setLenient(date, field, newValue);
        }
        return field.implementationDateRules(this).setLenient(date, field, newValue);
    }

    @Override
    public LocalTime setTimeLenient(LocalTime time, DateTimeField field, long newValue) {
        if (field instanceof StandardDateTimeField) {
            return ISOTimeRules.INSTANCE.setLenient(time, field, newValue);
        }
        return field.implementationTimeRules(this).setLenient(time, field, newValue);
    }

    @Override
    public LocalDateTime setDateTimeLenient(LocalDateTime dateTime, DateTimeField field, long newValue) {
        if (field instanceof StandardDateTimeField) {
            StandardDateTimeField std = (StandardDateTimeField) field;
            if (std.isDateField()) {
                return dateTime.with(setDateLenient(dateTime.toLocalDate(), field, newValue));
            } else {
                return dateTime.with(setTimeLenient(dateTime.toLocalTime(), field, newValue));
            }
        }
        return field.implementationDateTimeRules(this).setLenient(dateTime, field, newValue);
    }

    //-----------------------------------------------------------------------
    @Override
    public LocalDate rollDate(LocalDate date, DateTimeField field, long roll) {
        if (field instanceof StandardDateTimeField) {
            return ISODateRules.INSTANCE.roll(date, field, roll);
        }
        return field.implementationDateRules(this).roll(date, field, roll);
    }
    
    @Override
    public LocalTime rollTime(LocalTime time, DateTimeField field, long roll) {
        if (field instanceof StandardDateTimeField) {
            return ISOTimeRules.INSTANCE.roll(time, field, roll);
        }
        return field.implementationTimeRules(this).roll(time, field, roll);
    }

    @Override
    public LocalDateTime rollDateTime(LocalDateTime dateTime, DateTimeField field, long roll) {
        if (field instanceof StandardDateTimeField) {
            StandardDateTimeField standardField = (StandardDateTimeField) field;
            if (standardField.isDateField()) {
                return dateTime.with(rollDate(dateTime.toLocalDate(), field, roll));
            } else {
                return dateTime.with(rollTime(dateTime.toLocalTime(), field, roll));
            }
        } else {
            return field.implementationDateTimeRules(this).roll(dateTime, field, roll);
        }
    }

    //-----------------------------------------------------------------------
    @Override
    public LocalDate addToDate(LocalDate date, PeriodUnit unit, long amount) {
        if (unit instanceof StandardPeriodUnit) {
            switch ((StandardPeriodUnit) unit) {
                case DAYS: return date.plusDays(amount);
                case WEEKS: return date.plusWeeks(amount);
                case MONTHS: return date.plusMonths(amount);
                case QUARTER_YEARS: return date.plusMonths(MathUtils.safeMultiply(amount, 3));
                case HALF_YEARS: return date.plusMonths(MathUtils.safeMultiply(amount, 6));
                case YEARS: return date.plusYears(amount);
                case DECADES: return date.plusYears(MathUtils.safeMultiply(amount, 10));
                case CENTURIES: return date.plusYears(MathUtils.safeMultiply(amount, 100));
                case MILLENIA: return date.plusYears(MathUtils.safeMultiply(amount, 1000));
                case ERAS: {
                    if (amount == 0) {
                        return date;
                    } else if (amount == 1 && date.getYear() <= 0) {
                        return date.withYear(1 - date.getYear());
                    } else if (amount == -1 && date.getYear() > 0) {
                        return date.withYear(1 - date.getYear());
                    } else {
                        throw new CalendricalException("Unable to add eras: " + amount);
                    }
                }
            }
            throw new CalendricalException("Unsupported unit on LocalDate: " + unit);
        }
        return unit.implementationRules(this).addToDate(date, unit, amount);
    }

    @Override
    public LocalTime addToTime(LocalTime time, PeriodUnit unit, long amount) {
        if (unit instanceof StandardPeriodUnit) {
            switch ((StandardPeriodUnit) unit) {
                case NANOS: return time.plusNanos(amount);
                case MICROS: return time.plusNanos(MathUtils.safeMultiply(amount, 1000));
                case MILLIS: return time.plusNanos(MathUtils.safeMultiply(amount, 1000000));
                case SECONDS: return time.plusSeconds(amount);
                case MINUTES: return time.plusMinutes(amount);
                case HOURS: return time.plusHours(amount);
                case HALF_DAYS: return time.plusHours(MathUtils.safeMultiply(amount, 12));
            }
            throw new CalendricalException("Unsupported unit on LocalTime: " + unit);
        }
        return unit.implementationRules(this).addToTime(time, unit, amount);
    }

    @Override
    public LocalDateTime addToDateTime(LocalDateTime dateTime, PeriodUnit unit, long amount) {
        if (unit instanceof StandardPeriodUnit) {
            StandardPeriodUnit std = (StandardPeriodUnit) unit;
            if (std.ordinal() >= StandardPeriodUnit.DAYS.ordinal()) {
                return dateTime.with(addToDate(dateTime.toLocalDate(), unit, amount));
            } else {
                return dateTime.with(addToTime(dateTime.toLocalTime(), unit, amount));
            }
        }
        return unit.implementationRules(this).addToDateTime(dateTime, unit, amount);
    }

    //-----------------------------------------------------------------------
    @Override
    public long getPeriodBetweenDates(PeriodUnit unit, LocalDate date1, LocalDate date2) {
        return 0;
    }

    @Override
    public long getPeriodBetweenTimes(PeriodUnit unit, LocalTime time1, LocalTime time2) {
        return 0;
    }

    @Override
    public long getPeriodBetweenDateTimes(PeriodUnit unit, LocalDateTime dateTime1, LocalDateTime dateTime2) {
        return 0;
    }

    @Override
    public Duration getEstimatedDuration(PeriodUnit unit) {
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Rules implementing ISO calendar.
     */
    static class ISODateRules extends AbstractDateRules {
        static final DateRules INSTANCE = new ISODateRules();

        private static final DateTimeRuleRange RANGE_DOW = DateTimeRuleRange.of(1, 7);
        private static final DateTimeRuleRange RANGE_ERA = DateTimeRuleRange.of(0, 1);

        @Override
        public DateTimeRuleRange range(DateTimeField field, LocalDate date) {
            switch ((StandardDateTimeField) field) {
                case DAY_OF_WEEK: return RANGE_DOW;
                case DAY_OF_MONTH: {
                    if (date != null) {
                        return DateTimeRuleRange.of(1, date.getMonthOfYear().lengthInDays(date.isLeapYear()));
                    }
                    return DateTimeRuleRange.of(1, 28, 31);
                }
                case DAY_OF_YEAR: {
                    if (date != null) {
                        return DateTimeRuleRange.of(1, date.isLeapYear() ? 366 : 365);
                    }
                    return DateTimeRuleRange.of(1, 365, 366);
                }
                case EPOCH_DAY: return DateTimeRuleRange.of(MIN_EPOCH_DAY, MAX_EPOCH_DAY);
                case MONTH_OF_YEAR: return DateTimeRuleRange.of(1, 12);
                case EPOCH_MONTH: return DateTimeRuleRange.of(MIN_EPOCH_MONTH, MAX_EPOCH_MONTH);
                case YEAR_OF_ERA: return DateTimeRuleRange.of(1, MAX_YEAR);
                case YEAR: return DateTimeRuleRange.of(MIN_YEAR, MAX_YEAR);
                case ERA: return RANGE_ERA;
            }
            throw new CalendricalException("Unsupported field: " + field);
        }

        //-----------------------------------------------------------------------
        @Override
        public long get(LocalDate date, DateTimeField field) {
            switch ((StandardDateTimeField) field) {
                case ERA: return (date.getYear() > 0 ? 1 : 0);
                case YEAR: return date.getYear();
                case YEAR_OF_ERA: return (date.getYear() > 0 ? date.getYear() : 1 - date.getYear());
                case EPOCH_MONTH: return ((date.getYear() - 1970) * 12L) + date.getMonthOfYear().ordinal();
                case MONTH_OF_YEAR: return date.getMonthOfYear().getValue();
                case EPOCH_DAY: return date.toEpochDay();
                case DAY_OF_MONTH: return date.getDayOfMonth();
                case DAY_OF_YEAR: return date.getDayOfYear();
                case DAY_OF_WEEK: return date.getDayOfWeek().getValue();
            }
            throw new CalendricalException("Unsupported field: " + field);
        }

        //-------------------------------------------------------------------------
        @Override
        public LocalDate set(LocalDate date, DateTimeField field, long newValue) {
            if (range(field, date).isValidValue(newValue) == false) {
                throw new CalendricalException("Invalid value: " + field + " " + newValue);
            }
            switch ((StandardDateTimeField) field) {
                case ERA: {
                    if ((date.getYear() > 0 && newValue == 0) || (date.getYear() <= 0 && newValue == 1)) {
                        return date.withYear(1 - date.getYear());
                    }
                    return date;
                }
                case YEAR: return date.withYear((int) newValue);
                case YEAR_OF_ERA: return (date.getYear() > 0 ? date.withYear((int) newValue) : date.withYear((int) (1 - newValue)));
                case MONTH_OF_YEAR: return date.withMonthOfYear((int) newValue);
                case DAY_OF_MONTH: return date.withDayOfMonth((int) newValue);
                case DAY_OF_YEAR: return date.withDayOfYear((int) newValue);
                case DAY_OF_WEEK: return date.plusDays(newValue - date.getDayOfWeek().getValue());
            }
            throw new CalendricalException("Unsupported field: " + field);
        }

        //-------------------------------------------------------------------------
        @Override
        public LocalDate setLenient(LocalDate date, DateTimeField field, long newValue) {
            throw new CalendricalException("Unsupported field: " + field);
        }

        //-------------------------------------------------------------------------
        @Override
        public LocalDate roll(LocalDate date, DateTimeField field, long roll) {
            DateTimeRuleRange range = range(field, date);
            long valueRange = (range.getMaximum() - range.getMinimum()) + 1;
            long curValue0 = get(date, field) - 1;
            long newValue = ((curValue0 + (roll % valueRange)) % valueRange) + 1;
            return set(date, field, newValue);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Rules implementing ISO calendar.
     */
    static class ISOTimeRules implements TimeRules {
        static final TimeRules INSTANCE = new ISOTimeRules();

        private static final DateTimeRuleRange RANGE_NOS = DateTimeRuleRange.of(0, 999999999);
        private static final DateTimeRuleRange RANGE_NOD = DateTimeRuleRange.of(0, 86400L * 1000000000L - 1);
        private static final DateTimeRuleRange RANGE_MCOS = DateTimeRuleRange.of(0, 999999);
        private static final DateTimeRuleRange RANGE_MCOD = DateTimeRuleRange.of(0, 0, 86400L * 1000000L - 1);
        private static final DateTimeRuleRange RANGE_MLOS = DateTimeRuleRange.of(0, 999);
        private static final DateTimeRuleRange RANGE_MLOD = DateTimeRuleRange.of(0, 0, 86400L * 1000L - 1);
        private static final DateTimeRuleRange RANGE_SOM = DateTimeRuleRange.of(0, 59);
        private static final DateTimeRuleRange RANGE_SOD = DateTimeRuleRange.of(0, 86400L - 1);
        private static final DateTimeRuleRange RANGE_MOH = RANGE_SOM; //DateTimeRuleRange.of(0, 59);
        private static final DateTimeRuleRange RANGE_MOD = DateTimeRuleRange.of(0, 24 * 60 - 1);
        private static final DateTimeRuleRange RANGE_HOD = DateTimeRuleRange.of(0, 23);

        @Override
        public DateTimeRuleRange range(DateTimeField field, LocalTime time) {
            switch ((StandardDateTimeField) field) {
                case NANO_OF_SECOND: return RANGE_NOS;
                case NANO_OF_DAY: return RANGE_NOD;
                case MICRO_OF_SECOND: return RANGE_MCOS;
                case MICRO_OF_DAY: return RANGE_MCOD;
                case MILLI_OF_SECOND: return RANGE_MLOS;
                case MILLI_OF_DAY: return RANGE_MLOD;
                case SECOND_OF_MINUTE: return RANGE_SOM;
                case SECOND_OF_DAY: return RANGE_SOD;
                case MINUTE_OF_HOUR: return RANGE_MOH;
                case MINUTE_OF_DAY: return RANGE_MOD;
                case HOUR_OF_DAY: return RANGE_HOD;
            }
            throw new CalendricalException("Unsupported field: " + field);
        }

        //-----------------------------------------------------------------------
        @Override
        public long get(LocalTime time, DateTimeField field) {
            switch ((StandardDateTimeField) field) {
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
            throw new CalendricalException("Unsupported field: " + field);
        }

        //-------------------------------------------------------------------------
        @Override
        public LocalTime set(LocalTime time, DateTimeField field, long newValue) {
            if (range(field, time).isValidValue(newValue) == false) {
                throw new CalendricalException("Invalid value: " + field + " " + newValue);
            }
            switch ((StandardDateTimeField) field) {
                case HOUR_OF_DAY: return time.withHourOfDay((int) newValue);
                case MINUTE_OF_HOUR: return time.withMinuteOfHour((int) newValue);
                case SECOND_OF_MINUTE: return time.withSecondOfMinute((int) newValue);
                case MILLI_OF_SECOND: return time.withNanoOfSecond((int) newValue * 1000000);
                case MICRO_OF_SECOND: return time.withNanoOfSecond((int) newValue * 1000);
                case NANO_OF_SECOND: return time.withNanoOfSecond((int) newValue);
            }
            throw new CalendricalException("Unsupported field: " + field);
        }

        //-------------------------------------------------------------------------
        @Override
        public LocalTime setLenient(LocalTime time, DateTimeField field, long newValue) {
            throw new CalendricalException("Unsupported field: " + field);
        }

        //-------------------------------------------------------------------------
        @Override
        public LocalTime roll(LocalTime time, DateTimeField field, long roll) {
            DateTimeRuleRange range = range(field, time);
            long valueRange = (range.getMaximum() - range.getMinimum()) + 1;
            long curValue = get(time, field);
            long newValue = ((curValue + (roll % valueRange)) % valueRange);
            return set(time, field, newValue);
        }

        //-----------------------------------------------------------------------
        @Override
        public DateTimeRuleRange range(DateTimeField field, LocalDateTime dateTime) {
            return range(field, dateTime != null ? dateTime.toLocalTime() : null);
        }

        @Override
        public long get(LocalDateTime dateTime, DateTimeField field) {
            return get(dateTime.toLocalTime(), field);
        }

        @Override
        public LocalDateTime set(LocalDateTime dateTime, DateTimeField field, long newValue) {
            return dateTime.with(set(dateTime.toLocalTime(), field, newValue));
        }

        @Override
        public LocalDateTime setLenient(LocalDateTime dateTime, DateTimeField field, long newValue) {
            return dateTime.with(setLenient(dateTime.toLocalTime(), field, newValue));
        }

        @Override
        public LocalDateTime roll(LocalDateTime dateTime, DateTimeField field, long roll) {
            return dateTime.with(roll(dateTime.toLocalTime(), field, roll));
        }
    }

}
