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
import static javax.time.builder.StandardPeriodUnit.MONTHS;
import static javax.time.builder.StandardPeriodUnit.QUARTER_YEARS;
import static javax.time.builder.StandardPeriodUnit.YEARS;

import javax.time.CalendricalException;
import javax.time.LocalDate;
import javax.time.LocalDateTime;
import javax.time.LocalTime;
import javax.time.MathUtils;
import javax.time.calendrical.DateTimeRuleRange;

/**
 * A field of date/time.
 * 
 * @author Stephen Colebourne
 */
public enum QuarterYearDateTimeField implements DateTimeField {

    DAY_OF_QUARTER("DayOfQuarter", DAYS, QUARTER_YEARS),
    MONTH_OF_QUARTER("MonthOfQuarter", MONTHS, QUARTER_YEARS),
    QUARTER_OF_YEAR("QuarterOfYear", QUARTER_YEARS, YEARS);

    private static final int[] QUARTER_DAYS = {0, 90, 181, 273, 0, 91, 182, 274};

    private final String name;
    private final PeriodUnit baseUnit;
    private final PeriodUnit rangeUnit;

    private QuarterYearDateTimeField(String name, PeriodUnit baseUnit, PeriodUnit rangeUnit) {
        this.name = name;
        this.baseUnit = baseUnit;
        this.rangeUnit = rangeUnit;
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
    public Chrono getDefaultChronology() {
        return ISOChrono.INSTANCE;
    }

    @Override
    public DateTimeRules implementationRules(Chrono chronology) {
        if (chronology instanceof ISOChrono) {  // ISO specific?
            return Rules.INSTANCE;
        }
//        if (chronology.isSupported(StandardDateTimeField.MONTH_OF_YEAR)) {
//            return new Rules(chronology);
//        }
        throw new IllegalArgumentException("No rules for " + chronology.getName() + " " + getName());
    }

    @Override
    public String toString() {
        return getName();
    }

    //-----------------------------------------------------------------------
    /**
     * Rules implementing quarters.
     */
    static class Rules implements DateTimeRules {
        static final DateTimeRules INSTANCE = new Rules();

        private static final DateTimeRuleRange RANGE_DOQ = DateTimeRuleRange.of(1, 90, 92);
        private static final DateTimeRuleRange RANGE_DOQ_90 = DateTimeRuleRange.of(1, 90);
        private static final DateTimeRuleRange RANGE_DOQ_91 = DateTimeRuleRange.of(1, 91);
        private static final DateTimeRuleRange RANGE_DOQ_92 = DateTimeRuleRange.of(1, 92);
        private static final DateTimeRuleRange RANGE_MOQ = DateTimeRuleRange.of(1, 3);
        private static final DateTimeRuleRange RANGE_QOY = DateTimeRuleRange.of(1, 4);

        @Override
        public DateTimeRuleRange getRange(DateTimeField field) {
            switch ((QuarterYearDateTimeField) field) {
                case DAY_OF_QUARTER: return RANGE_DOQ;
                case MONTH_OF_QUARTER: return RANGE_MOQ;
                case QUARTER_OF_YEAR: return RANGE_QOY;
            }
            throw new CalendricalException("Unsupported field: " + field);
        }

        @Override
        public DateTimeRuleRange getRange(DateTimeField field, LocalDate date, LocalTime time) {
            if (date != null && field == QuarterYearDateTimeField.DAY_OF_QUARTER) {
                switch (date.getMonthOfYear().ordinal() / 3) {
                    case 0: return (date.isLeapYear() ? RANGE_DOQ_91 : RANGE_DOQ_90);
                    case 1: return RANGE_DOQ_91;
                    case 2: return RANGE_DOQ_92;
                    case 3: return RANGE_DOQ_92;
                }
            }
            return getRange(field);
        }

        //-----------------------------------------------------------------------
        @Override
        public long getDateValue(LocalDate date, DateTimeField field) {
            switch ((QuarterYearDateTimeField) field) {
                case DAY_OF_QUARTER: return doq(date);
                case MONTH_OF_QUARTER: return (date.getMonthOfYear().ordinal() % 3) + 1;
                case QUARTER_OF_YEAR: return (date.getMonthOfYear().ordinal() / 3) + 1;
            }
            throw new CalendricalException("Unsupported field: " + field);
        }

        @Override
        public long getTimeValue(LocalTime time, DateTimeField field) {
            throw new CalendricalException("Unsupported field on LocalTime: " + field);
        }

        @Override
        public long getDateTimeValue(LocalDateTime dateTime, DateTimeField field) {
            return getDateValue(dateTime.toLocalDate(), field);
        }

        private int doq(LocalDate date) {
            return date.getDayOfYear() - QUARTER_DAYS[(date.getMonthOfYear().ordinal() / 3) + (date.isLeapYear() ? 4 : 0)];
        }

        //-------------------------------------------------------------------------
        @Override
        public LocalDate setDate(LocalDate date, DateTimeField field, long newValue) {
            if (getRange(field, date, null).isValidValue(newValue) == false) {
                throw new CalendricalException("Invalid value: " + field + " " + newValue);
            }
            long value0 = newValue - 1;
            switch ((QuarterYearDateTimeField) field) {
                case DAY_OF_QUARTER: return date.plusDays(value0 - (doq(date) - 1));
                case MONTH_OF_QUARTER: return date.plusMonths(value0 - (date.getMonthOfYear().ordinal() % 3));
                case QUARTER_OF_YEAR: return date.plusMonths((value0 - (date.getMonthOfYear().ordinal() / 3)) * 3);
            }
            throw new CalendricalException("Unsupported field: " + field);
        }

        @Override
        public LocalTime setTime(LocalTime time, DateTimeField field, long newValue) {
            throw new CalendricalException("Unsupported field on LocalTime: " + field);
        }

        @Override
        public LocalDateTime setDateTime(LocalDateTime dateTime, DateTimeField field, long newValue) {
            return dateTime.with(setDate(dateTime.toLocalDate(), field, newValue));
        }

        //-------------------------------------------------------------------------
        @Override
        public LocalDate setDateLenient(LocalDate date, DateTimeField field, long newValue) {
            long value0 = MathUtils.safeDecrement(newValue);
            switch ((QuarterYearDateTimeField) field) {
                case DAY_OF_QUARTER: return date.plusDays(value0 - (doq(date) - 1));
                case MONTH_OF_QUARTER: return date.plusMonths(value0 - (date.getMonthOfYear().ordinal() % 3));
                case QUARTER_OF_YEAR: return date.plusMonths(MathUtils.safeMultiply(value0 - (date.getMonthOfYear().ordinal() / 3), 3));
            }
            throw new CalendricalException("Unsupported field: " + field);
        }

        @Override
        public LocalTime setTimeLenient(LocalTime time, DateTimeField field, long newValue) {
            throw new CalendricalException("Unsupported field on LocalTime: " + field);
        }

        @Override
        public LocalDateTime setDateTimeLenient(LocalDateTime dateTime, DateTimeField field, long newValue) {
            return dateTime.with(setDateLenient(dateTime.toLocalDate(), field, newValue));
        }

        //-------------------------------------------------------------------------
        @Override
        public LocalDate rollDate(LocalDate date, DateTimeField field, long roll) {
            DateTimeRuleRange range = getRange(field, date, null);
            long valueRange = (range.getMaximum() - range.getMinimum()) + 1;
            long curValue0 = getDateValue(date, field) - 1;
            long newValue = ((curValue0 + (roll % valueRange)) % valueRange) + 1;
            return setDate(date, field, newValue);
        }

        @Override
        public LocalTime rollTime(LocalTime time, DateTimeField field, long roll) {
            throw new CalendricalException("Unsupported field on LocalTime: " + field);
        }

        @Override
        public LocalDateTime rollDateTime(LocalDateTime dateTime, DateTimeField field, long roll) {
            return dateTime.with(rollDate(dateTime.toLocalDate(), field, roll));
        }
    }

}
