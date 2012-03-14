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
import javax.time.calendrical.DateTimeRuleRange;

/**
 * The ISO-8601 calendar system.
 * 
 * @author Stephen Colebourne
 */
public class ISOChrono implements Chrono, DateTimeRules {

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
    public DateTimeRuleRange getRange(DateTimeField field) {
        if (field instanceof StandardDateTimeField) {
            switch ((StandardDateTimeField) field) {
                case ERA: return DateTimeRuleRange.of(0, 1);
                case YEAR: return DateTimeRuleRange.of(MIN_YEAR, MAX_YEAR);
                case YEAR_OF_ERA: return DateTimeRuleRange.of(1, MAX_YEAR);
                case EPOCH_MONTH: return DateTimeRuleRange.of(MIN_EPOCH_MONTH, MAX_EPOCH_MONTH);
                case MONTH_OF_YEAR: return DateTimeRuleRange.of(1, 12);
                case EPOCH_DAY: return DateTimeRuleRange.of(MIN_EPOCH_DAY, MAX_EPOCH_DAY);
                case DAY_OF_MONTH: return DateTimeRuleRange.of(1, 28, 31);
                case DAY_OF_YEAR: return DateTimeRuleRange.of(1, 365, 366);
                case DAY_OF_WEEK: return DateTimeRuleRange.of(1, 7);
                case HOUR_OF_DAY: return DateTimeRuleRange.of(0, 23);
                case MINUTE_OF_HOUR: return DateTimeRuleRange.of(0, 59);
                case SECOND_OF_MINUTE: return DateTimeRuleRange.of(0, 59);
                case MILLI_OF_SECOND: return DateTimeRuleRange.of(0, 999);
                case MICRO_OF_SECOND: return DateTimeRuleRange.of(0, 999999);
                case NANO_OF_SECOND: return DateTimeRuleRange.of(0, 999999999);
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
        return null;
    }

    @Override
    public LocalTime setTimeLenient(LocalTime time, DateTimeField field, long newValue) {
        return null;
    }

    @Override
    public LocalDateTime setDateTimeLenient(LocalDateTime dateTime, DateTimeField field, long newValue) {
        return null;
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
        return null;
    }

    @Override
    public LocalTime addToTime(LocalTime time, PeriodUnit unit, long amount) {
        return null;
    }

    @Override
    public LocalDateTime addToDateTime(LocalDateTime dateTime, PeriodUnit unit, long amount) {
        return null;
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

}
