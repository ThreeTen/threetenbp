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
public class ISOChrono implements Chrono, DateTimeRules, PeriodRules {

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
    private static final long MIN_EPOCH_DAY = (long) (MIN_YEAR * 365.25);
    /**
     * The maximum permitted epoch-day.
     */
    private static final long MAX_EPOCH_DAY = (long) (MAX_YEAR * 365.25);

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
    private static final DateTimeRuleRange RANGE_DOW = DateTimeRuleRange.of(1, 7);
    private static final DateTimeRuleRange RANGE_ERA = DateTimeRuleRange.of(0, 1);

    @Override
    public String getName() {
        return "ISO";
    }

    //-----------------------------------------------------------------------
    @Override
    public DateTimeRuleRange getRange(DateTimeField field) {
        if (field instanceof StandardDateTimeField) {
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
                case DAY_OF_WEEK: return RANGE_DOW;
                case DAY_OF_MONTH: return DateTimeRuleRange.of(1, 28, 31);
                case DAY_OF_YEAR: return DateTimeRuleRange.of(1, 365, 366);
                case EPOCH_DAY: return DateTimeRuleRange.of(MIN_EPOCH_DAY, MAX_EPOCH_DAY);
                case MONTH_OF_YEAR: return DateTimeRuleRange.of(1, 12);
                case EPOCH_MONTH: return DateTimeRuleRange.of(MIN_EPOCH_MONTH, MAX_EPOCH_MONTH);
                case YEAR_OF_ERA: return DateTimeRuleRange.of(1, MAX_YEAR);
                case YEAR: return DateTimeRuleRange.of(MIN_YEAR, MAX_YEAR);
                case ERA: return RANGE_ERA;
            }
            throw new CalendricalException("Unsupported field");
        }
        return field.implementationRules(this).getRange(field);
    }

    @Override
    public DateTimeRuleRange getRange(DateTimeField field, LocalDate date, LocalTime time) {
        if (field instanceof StandardDateTimeField) {
            if (date != null) {
                switch ((StandardDateTimeField) field) {
                    case DAY_OF_MONTH: return DateTimeRuleRange.of(1, date.getMonthOfYear().lengthInDays(date.isLeapYear()));
                    case DAY_OF_YEAR: return DateTimeRuleRange.of(1, date.isLeapYear() ? 366 : 365);
                }
            }
            return getRange(field);
        }
        return field.implementationRules(this).getRange(field, date, time);
    }

    //-----------------------------------------------------------------------
    @Override
    public long getDateValue(LocalDate date, DateTimeField field) {
        if (field instanceof StandardDateTimeField) {
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
            throw new CalendricalException("Unsupported field");
        }
        return field.implementationRules(this).getDateValue(date, field);
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
        return field.implementationRules(this).getTimeValue(time, field);
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
        return field.implementationRules(this).getDateTimeValue(dateTime, field);
    }

    //-----------------------------------------------------------------------
    @Override
    public LocalDate setDate(LocalDate date, DateTimeField field, long newValue) {
        if (field instanceof StandardDateTimeField) {
            if (getRange(field, date, null).isValidValue(newValue) == false) {
                throw new IllegalArgumentException();  // TODO
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
                case EPOCH_DAY: return LocalDate.ofEpochDay(newValue);
            }
            throw new CalendricalException("Unsupported field on LocalDate: " + field);
        }
        return field.implementationRules(this).setDate(date, field, newValue);
    }

    @Override
    public LocalTime setTime(LocalTime time, DateTimeField field, long newValue) {
        if (field instanceof StandardDateTimeField) {
            if (getRange(field, null, time).isValidValue(newValue) == false) {
                throw new IllegalArgumentException();  // TODO
            }
            switch ((StandardDateTimeField) field) {
                case HOUR_OF_DAY: return time.withHourOfDay((int) newValue);
                case MINUTE_OF_HOUR: return time.withMinuteOfHour((int) newValue);
                case SECOND_OF_MINUTE: return time.withSecondOfMinute((int) newValue);
                case MILLI_OF_SECOND: return time.withNanoOfSecond((int) newValue * 1000000);
                case MICRO_OF_SECOND: return time.withNanoOfSecond((int) newValue * 1000);
                case NANO_OF_SECOND: return time.withNanoOfSecond((int) newValue);
            }
            throw new CalendricalException("Unsupported field on LocalTime: " + field);
        }
        return field.implementationRules(this).setTime(time, field, newValue);
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
        return field.implementationRules(this).setDateTime(dateTime, field, newValue);
    }

    //-----------------------------------------------------------------------
    @Override
    public LocalDate setDateLenient(LocalDate date, DateTimeField field, long newValue) {
        // TODO
        return field.implementationRules(this).setDateLenient(date, field, newValue);
    }

    @Override
    public LocalTime setTimeLenient(LocalTime time, DateTimeField field, long newValue) {
        // TODO
        return field.implementationRules(this).setTimeLenient(time, field, newValue);
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
        return field.implementationRules(this).setDateTimeLenient(dateTime, field, newValue);
    }

    //-----------------------------------------------------------------------
    @Override
    public LocalDate rollDate(LocalDate date, DateTimeField field, long roll) {
        DateTimeRuleRange range = getRange(field, date, null);
        long valueRange = (range.getMaximum() - range.getMinimum()) + 1;
        long currentValue = getDateValue(date, field);
        long newValue = roll % valueRange; // TODO
        return addToDate(date, field.getBaseUnit(), newValue - currentValue);
    }
    
    @Override
    public LocalTime rollTime(LocalTime time, DateTimeField field, long roll) {
        DateTimeRuleRange range = getRange(field, null, time);
        long valueRange = (range.getMaximum() - range.getMinimum()) + 1;
        long currentValue = getTimeValue(time, field);
        long newValue = roll % valueRange;
        return addToTime(time, field.getBaseUnit(), newValue - currentValue);
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
            return field.implementationRules(this).rollDateTime(dateTime, field, roll);
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

    @Override
    public LocalDate buildDate(DateTimeBuilder builder) {
        return builder.buildLocalDate();
    }

    @Override
    public LocalDateTime buildDateTime(DateTimeBuilder builder) {
        return LocalDateTime.of(buildDate(builder), builder.buildLocalTime());
    }
    
    @Override
    public DateChronoView<ISOChrono> buildDateChronoView(DateTimeBuilder builder) {
        return DateChronoView.of(buildDate(builder), this);
    }

}
