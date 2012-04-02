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
public class ISOChrono extends StandardChrono implements Chrono, DateTimeRules, PeriodRules {

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

    private static final DateTimeRuleRange RANGE_ERA = DateTimeRuleRange.of(0, 1);
    private static final DateTimeRuleRange RANGE_DOW = DateTimeRuleRange.of(1, 7);
    
    @Override
    public String getName() {
        return "ISO";
    }

    //-----------------------------------------------------------------------
    @Override
    public DateTimeRuleRange getDateValueRange(DateTimeField field, LocalDate date) {
        if (field instanceof ChronoField) {
            ChronoField standardField = (ChronoField) field;
            switch (standardField) {
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
        }
        return field.implementationRules(this).getDateValueRange(field, date);
    }

    //-----------------------------------------------------------------------
    @Override
    public long getDateValue(LocalDate date, DateTimeField field) {
        if (field instanceof ChronoField) {
            switch ((ChronoField) field) {
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
    public LocalDate setDate(LocalDate date, DateTimeField field, long newValue) {
        if (field instanceof ChronoField) {
            if (getDateValueRange(field, date).isValidValue(newValue) == false) {
                throw new IllegalArgumentException();  // TODO
            }
            switch ((ChronoField) field) {
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

    //-----------------------------------------------------------------------
    @Override
    public LocalDate setDateLenient(LocalDate date, DateTimeField field, long newValue) {
        // TODO
        return field.implementationRules(this).setDateLenient(date, field, newValue);
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
    @Override
    public LocalDate buildDate(DateTimeBuilder builder) {
        return builder.buildLocalDate();
    }

}
