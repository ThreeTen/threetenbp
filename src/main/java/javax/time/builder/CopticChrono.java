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

import static javax.time.MathUtils.safeToInt;

import javax.time.CalendricalException;
import javax.time.Duration;
import javax.time.LocalDate;
import javax.time.LocalDateTime;
import javax.time.LocalTime;
import javax.time.calendrical.DateTimeRuleRange;

/**
 * The Coptic calendar system.
 * <p>
 * This chronology defines the rules of the Coptic calendar system.
 * The Coptic calendar has twelve months of 30 days followed by an additional
 * period of 5 or 6 days, modeled as the thirteenth month in this implementation.
 * <p>
 * Years are measured in the 'Era of the Martyrs' - AM.
 * 0001-01-01 (Coptic) equals 0284-08-29 (ISO).
 * The supported range is from 1 to 99999999 (inclusive) in both eras.
 * <p>
 * This class is immutable and thread-safe.
 *
 * NB: currently exploratory, represented ranges should be rechecked, ignores eras, doesn't check ranges in arithmetic
 *
 * @author Richard Warburton
 */
public enum CopticChrono implements Chrono {

    INSTANCE;

    private static final int MONTHS_PER_YEAR = 13;

    /**
     * This is a Non-proleptic Calendar, and it starts at year 1.
     */
    private static final int MIN_YEAR = 1;
    /**
     * The maximum permitted year.
     */
    private static final int MAX_YEAR = 999999999;
    /**
     * The minimum permitted epoch-month.
     */
    private static final long MIN_EPOCH_MONTH = (MIN_YEAR - 1970L) * MONTHS_PER_YEAR;
    /**
     * The maximum permitted epoch-month.
     */
    private static final long MAX_EPOCH_MONTH = (MAX_YEAR - 1970L) * MONTHS_PER_YEAR - 1L;
    /**
     * The minimum permitted epoch-day.
     */
    private static final long MIN_EPOCH_DAY = 0;
    /**
     * The maximum permitted epoch-day.
     */
    private static final long MAX_EPOCH_DAY = 0;
    
    private static final long EPOCH_DAYS_OFFSET = 615558;
    
    private static final long YEAR_BLOCK = 3 * 365 + 366;

    @Override
    public String getName() {
        return "Coptic";
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
                case MONTH_OF_YEAR: return DateTimeRuleRange.of(1, 13);
                case EPOCH_DAY: return DateTimeRuleRange.of(MIN_EPOCH_DAY, MAX_EPOCH_DAY);
                case DAY_OF_MONTH: return DateTimeRuleRange.of(1, 5, 30);
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
                    case DAY_OF_MONTH:
                        if (getMonthOfYear(date) == 13) {
                            return DateTimeRuleRange.of(1, isLeapYear(date) ? 6 : 5);
                        }
                        return DateTimeRuleRange.of(1, 30);
                    case DAY_OF_YEAR: return DateTimeRuleRange.of(1, isLeapYear(date) ? 366 : 365);
                }
            }
            return getRange(field);
        }
        return field.implementationRules(this).getRange(field, date, time);
    }

    private boolean isLeapYear(LocalDate date) {
        return (getYear(date) % 4) == 0;
    }

    //-----------------------------------------------------------------------
    @Override
    public long getDateValue(LocalDate date, DateTimeField field) {
        if (field instanceof StandardDateTimeField) {
            switch ((StandardDateTimeField) field) {
                case ERA: return (getYear(date) > 0 ? 1 : 0);   // TODO
                case YEAR: return getYear(date);
                case YEAR_OF_ERA: return (getYear(date) > 0 ? getYear(date) : 1 - getYear(date));   // TODO
                case EPOCH_MONTH: return ((getYear(date) - 1970) * 13L) + getMonthOfYear(date);
                case MONTH_OF_YEAR: return getMonthOfYear(date);
                case EPOCH_DAY: return getEpochDay(date);
                case DAY_OF_MONTH: return getDayOfMonth(date);
                case DAY_OF_YEAR: return getDayOfYear(date);
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
            return ISOChrono.INSTANCE.getTimeValue(time, field);
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

    /**
     * Abstracted common logic, readability.
     */
    private long getMonthOfYear(LocalDate date) {
        return 1 + (getDayOfYear(date) / 30);
    }
    
    private long getYear(LocalDate date) {
        long epochDay = getEpochDay(date);
        long yearPrefix = (epochDay / YEAR_BLOCK) * 4;
        long yearSuffix = (epochDay % YEAR_BLOCK) / 365;
        return 1 + (yearPrefix + yearSuffix);
    }
    
    private long getDayOfYear(LocalDate date) {
        long blockNumber = getEpochDay(date) % YEAR_BLOCK;
        long leapSum = (blockNumber / 365) == 3 ? 0 : 1;
        return leapSum + (blockNumber % 365);
    }
    
    private long getEpochDay(LocalDate date) {
        return EPOCH_DAYS_OFFSET + date.toEpochDay();
    }
    
    private long getDayOfMonth(LocalDate date) {
        return getDayOfYear(date) - (getMonthOfYear(date) - 1) * 30;
    }

    //-----------------------------------------------------------------------
    @Override
    public LocalDate setDate(LocalDate date, DateTimeField field, long newValue) {
        if (field instanceof StandardDateTimeField) {
            if (getRange(field, date, null).isValidValue(newValue) == false) {
                throw new IllegalArgumentException();  // TODO
            }
            switch ((StandardDateTimeField) field) {
                case ERA: {   // TODO
                    if ((getYear(date) > 0 && newValue == 0) && (getYear(date)) <= 0 && newValue == 1) {
                        return date.withYear(1 - safeToInt(getYear(date)));
                    }
                    return date;
                }
                case YEAR: return setYear(date, newValue);
                case YEAR_OF_ERA: throw new UnsupportedOperationException("Not implemented yet");
                case EPOCH_MONTH: return setMonthOfYear(setYear(date, newValue / MONTHS_PER_YEAR), newValue % MONTHS_PER_YEAR);
                case MONTH_OF_YEAR: return setMonthOfYear(date, newValue);
                case EPOCH_DAY: return LocalDate.ofEpochDay(newValue - 615558);
                case DAY_OF_MONTH: return setDayOfYear(date, safeToInt((getMonthOfYear(date) - 1) * 30 + newValue));
                case DAY_OF_YEAR: return setDayOfYear(date, newValue);
                case DAY_OF_WEEK: return date.plusDays(newValue - date.getDayOfWeek().getValue());
            }
            throw new CalendricalException("Unsupported field on LocalDate: " + field);
        }
        return field.implementationRules(this).setDate(date, field, newValue);
    }
    
    private LocalDate setYear(LocalDate date, long newValue) {
        long diff = newValue - getYear(date);
        return date.plusYears(diff);
    }
    
    private LocalDate setDayOfYear(LocalDate date, long newValue) {
        long diff = newValue - getDayOfYear(date);
        return date.plusDays(diff);
    }
    
    private LocalDate setMonthOfYear(LocalDate date, long newValue) {
        long diff = newValue - getMonthOfYear(date);
        return date.plusMonths(diff);
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
        long newValue = roll % valueRange;  // TODO
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
