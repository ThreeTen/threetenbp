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
import static javax.time.builder.StandardPeriodUnit.FOREVER;
import static javax.time.builder.StandardPeriodUnit.MINUTES;
import static javax.time.builder.StandardPeriodUnit.MONTHS;
import static javax.time.builder.StandardPeriodUnit.WEEKS;
import static javax.time.builder.StandardPeriodUnit.YEARS;

import javax.time.CalendricalException;
import javax.time.LocalDate;
import javax.time.LocalDateTime;
import javax.time.MonthOfYear;
import javax.time.calendrical.DateTimeRuleRange;

/**
 * A field of date.
 * 
 * @author Stephen Colebourne
 */
public enum LocalDateField implements DateField {

    DAY_OF_WEEK("DayOfWeek", DAYS, WEEKS),
    ALIGNED_DAY_OF_WEEK_IN_MONTH("AlignedDayOfWeekInMonth", DAYS, WEEKS),
    ALIGNED_DAY_OF_WEEK_IN_YEAR("AlignedDayOfWeekInYear", DAYS, WEEKS),
    DAY_OF_MONTH("DayOfMonth", DAYS, MONTHS),
    DAY_OF_YEAR("DayOfYear", DAYS, MINUTES),
    EPOCH_DAY("EpochDay", DAYS, FOREVER),
    ALIGNED_WEEK_OF_MONTH("AlignedWeekOfMonth", WEEKS, MONTHS),
    ALIGNED_WEEK_OF_YEAR("AlignedWeekOfYear", WEEKS, YEARS),
    MONTH_OF_YEAR("MonthOfYear", MONTHS, YEARS),
    EPOCH_MONTH("EpochMonth", MONTHS, FOREVER),
    YEAR("Year", YEARS, FOREVER);

    private final String name;
    private final PeriodUnit baseUnit;
    private final PeriodUnit rangeUnit;
    private final DRules dRules;
    private final DTRules dtRules;

    private LocalDateField(String name, PeriodUnit baseUnit, PeriodUnit rangeUnit) {
        this.name = name;
        this.baseUnit = baseUnit;
        this.rangeUnit = rangeUnit;
        this.dRules = new DRules(this);
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
    public DateTimeRules<LocalDate> getDateRules() {
        return dRules;
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
    /**
     * Date rules for the field.
     */
    private static final class DRules implements DateTimeRules<LocalDate> {
        private static final int MIN_YEAR = -999999998;
        private static final int MAX_YEAR = 999999999;
        private static final long MIN_EPOCH_MONTH = (MIN_YEAR - 1970L) * 12L;
        private static final long MAX_EPOCH_MONTH = (MAX_YEAR - 1970L) * 12L - 1L;
        private static final long MIN_EPOCH_DAY = (long) (MIN_YEAR * 365.25);
        private static final long MAX_EPOCH_DAY = (long) (MAX_YEAR * 365.25);
        private static final DateTimeRuleRange RANGE_DOW = DateTimeRuleRange.of(1, 7);
        private static final DateTimeRuleRange RANGE_DOM = DateTimeRuleRange.of(1, 28, 31);
        private static final DateTimeRuleRange RANGE_DOY = DateTimeRuleRange.of(1, 365, 366);
        private static final DateTimeRuleRange RANGE_ED = DateTimeRuleRange.of(MIN_EPOCH_DAY, MAX_EPOCH_DAY);
        private static final DateTimeRuleRange RANGE_MOY = DateTimeRuleRange.of(1, 12);
        private static final DateTimeRuleRange RANGE_EM = DateTimeRuleRange.of(MIN_EPOCH_MONTH, MAX_EPOCH_MONTH);
        private static final DateTimeRuleRange RANGE_Y = DateTimeRuleRange.of(MIN_YEAR, MAX_YEAR);

        private final LocalDateField field;
        private DRules(LocalDateField field) {
            this.field = field;
        }
        @Override
        public DateTimeRuleRange range() {
            switch (field) {
                case DAY_OF_WEEK: return RANGE_DOW;
                case ALIGNED_DAY_OF_WEEK_IN_MONTH: return RANGE_DOW;
                case ALIGNED_DAY_OF_WEEK_IN_YEAR: return RANGE_DOW;
                case DAY_OF_MONTH: return RANGE_DOM;
                case DAY_OF_YEAR: return RANGE_DOY;
                case ALIGNED_WEEK_OF_MONTH: return DateTimeRuleRange.of(1, 4, 5);
                case ALIGNED_WEEK_OF_YEAR: return DateTimeRuleRange.of(1, 53);
                case EPOCH_DAY: return RANGE_ED;
                case MONTH_OF_YEAR: return RANGE_MOY;
                case EPOCH_MONTH: return RANGE_EM;
                case YEAR: return RANGE_Y;
            }
            throw new CalendricalException("Unknown field");
        }
        @Override
        public DateTimeRuleRange range(LocalDate date) {
            switch (field) {
                case DAY_OF_MONTH: return DateTimeRuleRange.of(1, date.getMonthOfYear().lengthInDays(date.isLeapYear()));
                case DAY_OF_YEAR: return DateTimeRuleRange.of(1, date.isLeapYear() ? 366 : 365);
                case ALIGNED_WEEK_OF_MONTH: return DateTimeRuleRange.of(1, 
                                date.getMonthOfYear() == MonthOfYear.FEBRUARY && date.isLeapYear() == false ? 4 : 5);
            }
            return range();
        }
        @Override
        public long get(LocalDate date) {
            switch (field) {
                case DAY_OF_WEEK: return date.getDayOfWeek().getValue();
                case ALIGNED_DAY_OF_WEEK_IN_MONTH: return ((date.getDayOfMonth() - 1) % 7) + 1;
                case ALIGNED_DAY_OF_WEEK_IN_YEAR: return ((date.getDayOfYear() - 1) % 7) + 1;
                case DAY_OF_MONTH: return date.getDayOfMonth();
                case DAY_OF_YEAR: return date.getDayOfYear();
                case ALIGNED_WEEK_OF_MONTH: return ((date.getDayOfMonth() - 1) / 7) + 1;
                case ALIGNED_WEEK_OF_YEAR: return ((date.getDayOfYear() - 1) / 7) + 1;
                case EPOCH_DAY: return date.toEpochDay();
                case MONTH_OF_YEAR: return date.getMonthOfYear().getValue();
                case EPOCH_MONTH: return ((date.getYear() - 1970) * 12L) + date.getMonthOfYear().ordinal();
                case YEAR: return date.getYear();
            }
            throw new CalendricalException("Unsupported field");
        }
        @Override
        public LocalDate set(LocalDate date, long newValue) {
            if (range(date).isValidValue(newValue) == false) {
                throw new CalendricalException("Invalid value: " + field + " " + newValue);
            }
            switch (field) {
                case YEAR: return date.withYear((int) newValue);
                case MONTH_OF_YEAR: return date.withMonthOfYear((int) newValue);
                case DAY_OF_MONTH: return date.withDayOfMonth((int) newValue);
                case DAY_OF_YEAR: return date.withDayOfYear((int) newValue);
                case DAY_OF_WEEK: return date.plusDays(newValue - date.getDayOfWeek().getValue());
                case EPOCH_DAY: return LocalDate.ofEpochDay(newValue);
            }
            throw new CalendricalException("Unsupported field");
        }
        @Override
        public LocalDate setLenient(LocalDate date, long newValue) {
            return null;  // TODO
        }
        @Override
        public LocalDate roll(LocalDate date, long roll) {
            return null;  // TODO
//            DateTimeRuleRange range = range(date);
//            long valueRange = (range.getMaximum() - range.getMinimum()) + 1;
//            long currentValue = get(date);
//            long newValue = roll % valueRange; // TODO
//            return addToDate(date, field.getBaseUnit(), newValue - currentValue);
        }
    }

    //-------------------------------------------------------------------------
    /**
     * Date-time rules for the field.
     */
    private static final class DTRules implements DateTimeRules<LocalDateTime> {
        private final DateTimeRules<LocalDate> rules;
        private DTRules(LocalDateField field) {
            this.rules = field.getDateRules();
        }
        @Override
        public DateTimeRuleRange range() {
            return rules.range();
        }
        @Override
        public DateTimeRuleRange range(LocalDateTime dateTime) {
            return rules.range(dateTime.toLocalDate());
        }
        @Override
        public long get(LocalDateTime dateTime) {
            return rules.get(dateTime.toLocalDate());
        }
        @Override
        public LocalDateTime set(LocalDateTime dateTime, long newValue) {
            return dateTime.with(rules.set(dateTime.toLocalDate(), newValue));
        }
        @Override
        public LocalDateTime setLenient(LocalDateTime dateTime, long newValue) {
            return dateTime.with(rules.setLenient(dateTime.toLocalDate(), newValue));
        }
        @Override
        public LocalDateTime roll(LocalDateTime dateTime, long roll) {
            return dateTime.with(rules.roll(dateTime.toLocalDate(), roll));
        }
    }

}
