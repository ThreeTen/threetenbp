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

import javax.time.Duration;
import javax.time.LocalDate;
import javax.time.LocalDateTime;
import javax.time.LocalTime;
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
    public DateTimeRules getImplementationRules(Chrono chronology) {
        if (chronology instanceof ISOChrono) {  // ISO specific?
            return Rules.INSTANCE;
        }
//        if (chronology.isSupported(StandardDateTimeField.MONTH_OF_YEAR)) {
//            return new Rules(chronology);
//        }
        throw new IllegalArgumentException("No rules for " + chronology.getName() + " " + getName());
    }

    //-----------------------------------------------------------------------
    /**
     * Rules implementing quarters.
     */
    static class Rules implements DateTimeRules {
        static final DateTimeRules INSTANCE = new Rules();

        @Override
        public DateTimeRuleRange getRange(DateTimeField field) {
            switch ((QuarterYearDateTimeField) field) {
                case DAY_OF_QUARTER: return DateTimeRuleRange.of(1, 90, 92);
                case MONTH_OF_QUARTER: return DateTimeRuleRange.of(1, 3);
                case QUARTER_OF_YEAR: return DateTimeRuleRange.of(1, 4);
            }
            throw new IllegalArgumentException("Unknown field");
        }

        @Override
        public DateTimeRuleRange getRange(DateTimeField field, LocalDate date, LocalTime time) {
            switch ((QuarterYearDateTimeField) field) {
                case DAY_OF_QUARTER: {
                    if (date != null) {
                        switch (date.getMonthOfYear().ordinal() / 3) {
                            case 0: return DateTimeRuleRange.of(1, date.isLeapYear() ? 91 : 90);
                            case 1: return DateTimeRuleRange.of(1, 91);
                            case 2:
                            case 3:
                                return DateTimeRuleRange.of(1, 92);
                        }
                    }
                    return DateTimeRuleRange.of(1, 90, 92);
                }
                case MONTH_OF_QUARTER: return DateTimeRuleRange.of(1, 3);
                case QUARTER_OF_YEAR: return DateTimeRuleRange.of(1, 4);
            }
            throw new IllegalArgumentException("Unknown field");
        }

        @Override
        public long getValue(DateTimeField field, LocalDate date, LocalTime time) {
            if (date != null) {
                long moy0 = date.getMonthOfYear().ordinal();
                switch ((QuarterYearDateTimeField) field) {
                    case DAY_OF_QUARTER:
                        long dom = date.getDayOfMonth();
                        return date.getMonthOfYear().getMonthStartDayOfYear(date.isLeapYear()) - 1 + dom;  // TODO
                    case MONTH_OF_QUARTER: return ((moy0) % 3) + 1;
                    case QUARTER_OF_YEAR: return ((moy0) / 3) + 1;
                }
                throw new IllegalArgumentException("Unknown field");
            }
            return 0;  // TODO
        }

        @Override
        public LocalDate setDate(DateTimeField field, LocalDate date, long newValue) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public LocalTime setTime(DateTimeField field, LocalTime time, long newValue) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public LocalDateTime setDateTime(DateTimeField field, LocalDateTime dateTime, long newValue) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public LocalDate setDateLenient(DateTimeField field, LocalDate date, long newValue) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public LocalTime setTimeLenient(DateTimeField field, LocalTime time, long newValue) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public LocalDateTime setDateTimeLenient(DateTimeField field, LocalDateTime dateTime, long newValue) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public LocalDate addToDate(DateTimeField field, LocalDate date, long amount) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public LocalTime addToTime(DateTimeField field, LocalTime time, long amount) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public LocalDateTime addToDateTime(DateTimeField field, LocalDateTime dateTime, long amount) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public LocalDate rollDate(DateTimeField field, LocalDate date, long roll) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public LocalTime rollTime(DateTimeField field, LocalTime time, long roll) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public LocalDateTime rollDateTime(DateTimeField field, LocalDateTime dateTime, long roll) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Duration getEstimatedDuration(PeriodUnit unit) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Duration getDurationBetween(LocalDate date1, LocalTime time1, LocalDate date2, LocalTime time2) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getPeriodBetween(PeriodUnit unit, LocalDate date1, LocalTime time1, LocalDate date2, LocalTime time2) {
            // TODO Auto-generated method stub
            return 0;
        }
        
    }

}
