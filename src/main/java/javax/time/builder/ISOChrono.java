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

import javax.time.Duration;
import javax.time.LocalDate;
import javax.time.LocalDateTime;
import javax.time.LocalTime;
import javax.time.calendrical.DateTimeRuleRange;

/**
 * A calendar system.
 * 
 * @author Stephen Colebourne
 */
public class ISOChrono implements Chrono {

    /**
     * The minimum permitted year.
     */
    public static final int MIN_YEAR = -999999998;
    /**
     * The maximum permitted year.
     */
    public static final int MAX_YEAR = 999999999;

    @Override
    public String getName() {
        return "ISO";
    }

    //-----------------------------------------------------------------------
    @Override
    public DateTimeRuleRange getRange(DateTimeField field) {
        if (field instanceof StandardDateTimeField) {
            switch ((StandardDateTimeField) field) {
                case ERA: return DateTimeRuleRange.of(MIN_YEAR, MAX_YEAR);
                case YEAR: return DateTimeRuleRange.of(MIN_YEAR, MAX_YEAR);
                case YEAR_OF_ERA: return DateTimeRuleRange.of(1, MAX_YEAR);
                case MONTH_OF_YEAR: return DateTimeRuleRange.of(1, 12);
                case DAY_OF_MONTH: return DateTimeRuleRange.of(1, 28, 31);
                case DAY_OF_YEAR: return DateTimeRuleRange.of(1, 365, 366);
                case DAY_OF_WEEK: return DateTimeRuleRange.of(1, 7);
                case HOUR_OF_DAY: return DateTimeRuleRange.of(0, 23);
                case MINUTE_OF_HOUR: return DateTimeRuleRange.of(0, 59);
                case SECOND_OF_MINUTE: return DateTimeRuleRange.of(0, 59);
            }
        }
        return null;
    }

    @Override
    public DateTimeRuleRange getRange(DateTimeField field, LocalDate date, LocalTime time) {
        if (date != null && field instanceof StandardDateTimeField) {
            switch ((StandardDateTimeField) field) {
                case DAY_OF_MONTH: return DateTimeRuleRange.of(1, date.getMonthOfYear().lengthInDays(date.isLeapYear()));
                case DAY_OF_YEAR: return date.isLeapYear() ? DateTimeRuleRange.of(1, 366) :  DateTimeRuleRange.of(1, 365);
            }
        }
        return getRange(field);
    }

    //-----------------------------------------------------------------------
    @Override
    public int getValue(DateTimeField field, LocalDate date, LocalTime time) {
        if (field instanceof StandardDateTimeField) {
            if (date != null) {
                switch ((StandardDateTimeField) field) {
                    case ERA: return (date.getYear() > 0 ? 1 : 0);
                    case YEAR: return date.getYear();
                    case YEAR_OF_ERA: return (date.getYear() > 0 ? date.getYear() : -date.getYear());
                    case MONTH_OF_YEAR: return date.getMonthOfYear().getValue();
                    case DAY_OF_MONTH: return date.getDayOfMonth();
                    case DAY_OF_YEAR: return date.getDayOfYear();
                    case DAY_OF_WEEK: return date.getDayOfWeek().getValue();
                }
            }
            if (time != null) {
                switch ((StandardDateTimeField) field) {
                    case HOUR_OF_DAY: return time.getHourOfDay();
                    case MINUTE_OF_HOUR: return time.getMinuteOfHour();
                    case SECOND_OF_MINUTE: return time.getSecondOfMinute();
                }
            }
        }
        return Integer.MIN_VALUE;  // TODO: exception or quiet
    }

    //-----------------------------------------------------------------------
    @Override
    public LocalDate setDate(DateTimeField field, LocalDate date, int newValue) {
        if (field instanceof StandardDateTimeField) {
            if (getRange(field, date, null).isValidValue(newValue) == false) {
                throw new IllegalArgumentException();  // TODO
            }
            switch ((StandardDateTimeField) field) {
                case ERA: {
                    if ((date.getYear() > 0 && newValue == 0) && (date.getYear() <= 0 && newValue == 1)) {
                        return date.withYear(1 - date.getYear());
                    }
                    return date;
                }
                case YEAR: return date.withYear(newValue);
                case YEAR_OF_ERA: return (date.getYear() > 0 ? date.withYear(newValue) : date.withYear(1 - newValue));
                case MONTH_OF_YEAR: return date.withMonthOfYear(newValue);
                case DAY_OF_MONTH: return date.withDayOfMonth(newValue);
                case DAY_OF_YEAR: return date.withDayOfYear(newValue);
                case DAY_OF_WEEK: return date.plusDays(newValue - date.getDayOfWeek().getValue());
            }
            return date;
//            throw new IllegalArgumentException("Unable to set field in date " + field);
        }
        return null;  // TODO
    }

    @Override
    public LocalTime setTime(DateTimeField field, LocalTime time, int newValue) {
        if (field instanceof StandardDateTimeField) {
            if (getRange(field, null, time).isValidValue(newValue) == false) {
                throw new IllegalArgumentException();  // TODO
            }
            switch ((StandardDateTimeField) field) {
                case HOUR_OF_DAY: return time.withHourOfDay(newValue);
                case MINUTE_OF_HOUR: return time.withMinuteOfHour(newValue);
                case SECOND_OF_MINUTE: return time.withSecondOfMinute(newValue);
            }
            return time;
        }
        return null;  // TODO
    }

    @Override
    public LocalDateTime setDateTime(DateTimeField field, LocalDateTime dateTime, int newValue) {
        if (field instanceof StandardDateTimeField) {
            StandardDateTimeField std = (StandardDateTimeField) field;
            if (std.isDateField()) {
                return dateTime.with(setDate(field, dateTime.toLocalDate(), newValue));
            } else {
                return dateTime.with(setTime(field, dateTime.toLocalTime(), newValue));
            }
        }
        return null;
    }

    //-----------------------------------------------------------------------
    @Override
    public LocalDate setDateLenient(DateTimeField field, LocalDate date, int newValue) {
        return null;
    }

    @Override
    public LocalTime setTimeLenient(DateTimeField field, LocalTime time, int newValue) {
        return null;
    }

    @Override
    public LocalDateTime setDateTimeLenient(DateTimeField field, LocalDateTime dateTime, int newValue) {
        return null;
    }

    //-----------------------------------------------------------------------
    @Override
    public LocalDate addDate(DateTimeField field, LocalDate date, int amount) {
        return null;
    }

    @Override
    public LocalDate rollDate(DateTimeField field, LocalDate date, int roll) {
        return null;
    }

    @Override
    public Duration getEstimatedDuration(PeriodUnit unit) {
        return null;
    }

    @Override
    public Duration getDurationBetween(LocalDate date1, LocalTime time1,
            LocalDate date2, LocalTime time2) {
        return null;
    }

    @Override
    public long getPeriodBetween(PeriodUnit unit, LocalDate date1, LocalTime time1, LocalDate date2, LocalTime time2) {
        return 0;
    }

}
