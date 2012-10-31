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
package javax.time.extra;

import static javax.time.calendrical.LocalDateTimeField.DAY_OF_MONTH;
import static javax.time.calendrical.LocalDateTimeField.DAY_OF_YEAR;
import static javax.time.calendrical.LocalDateTimeField.MONTH_OF_YEAR;
import static javax.time.calendrical.LocalDateTimeField.YEAR;
import static javax.time.calendrical.LocalPeriodUnit.DAYS;
import static javax.time.calendrical.LocalPeriodUnit.MONTHS;
import static javax.time.calendrical.LocalPeriodUnit.QUARTER_YEARS;
import static javax.time.calendrical.LocalPeriodUnit.YEARS;

import javax.time.DateTimes;
import javax.time.Instant;
import javax.time.LocalDate;
import javax.time.LocalDateTime;
import javax.time.LocalTime;
import javax.time.Month;
import javax.time.OffsetDateTime;
import javax.time.ZonedDateTime;
import javax.time.calendrical.DateTimeAccessor;
import javax.time.calendrical.DateTimeBuilder;
import javax.time.calendrical.DateTimeField;
import javax.time.calendrical.DateTimeValueRange;
import javax.time.calendrical.PeriodUnit;

/**
 * A set of date fields that provide access to the quarter-of-year.
 * 
 * <h4>Implementation notes</h4>
 * This is an immutable and thread-safe enum.
 */
public enum QuarterYearField implements DateTimeField {

    /**
     * The day-of-quarter.
     * This counts the day within the quarter, from 1 to 92.
     * Quarters have different lengths, from 90 to 92 days.
     */
    DAY_OF_QUARTER("DayOfQuarter", DAYS, QUARTER_YEARS, DateTimeValueRange.of(1, 90, 92)) {
        @Override
        public DateTimeValueRange doRange(DateTimeAccessor dateTime) {
            if (QUARTER_OF_YEAR.isSupported(dateTime)) {
                long qoy = dateTime.getLong(QUARTER_OF_YEAR);
                if (qoy == 1) {
                    if (YEAR.isSupported(dateTime)) {
                        long year = dateTime.getLong(YEAR);
                        return (DateTimes.isLeapYear(year) ? RANGE_DOQ_91 : RANGE_DOQ_90);
                    } else {
                        return RANGE_DOQ_90_91;
                    }
                } else if (qoy == 2) {
                    return RANGE_DOQ_91;
                } else if (qoy == 3 || qoy == 4) {
                    return RANGE_DOQ_92;
                } // else value not from 1 to 4, so drop through
            }
            return range();
        }
        @Override
        public long doGet(DateTimeAccessor dateTime) {
            int doy = dateTime.get(DAY_OF_YEAR);
            int moy = dateTime.get(MONTH_OF_YEAR);
            long year = dateTime.getLong(YEAR);
            return doy - QUARTER_DAYS[((moy - 1) / 3) + (DateTimes.isLeapYear(year) ? 4 : 0)];
        }
        @Override
        public <R extends DateTimeAccessor> R doSet(R dateTime, long newValue) {
            long curValue = doGet(dateTime);
            doRange(dateTime).checkValidValue(newValue, this);
            return (R) dateTime.with(DAY_OF_YEAR, dateTime.getLong(DAY_OF_YEAR) + (newValue - curValue));
        }
    },
    /**
     * The month-of-quarter.
     * This counts the month within the quarter, from 1 to 3.
     */
    MONTH_OF_QUARTER("MonthOfQuarter", MONTHS, QUARTER_YEARS,  DateTimeValueRange.of(1, 3)) {
        @Override
        public DateTimeValueRange doRange(DateTimeAccessor dateTime) {
            return range();
        }
        @Override
        public long doGet(DateTimeAccessor dateTime) {
            return ((dateTime.getLong(MONTH_OF_YEAR) - 1) % 3) + 1;
        }

        @Override
        public <R extends DateTimeAccessor> R doSet(R dateTime, long newValue) {
            long curValue = doGet(dateTime);
            range().checkValidValue(newValue, this);
            return (R) dateTime.with(MONTH_OF_YEAR, dateTime.getLong(MONTH_OF_YEAR) + (newValue - curValue));
        }
    },
    /**
     * The quarter-of-year.
     * This counts the quarter within the year, from 1 to 4.
     * This is typically expressed as Q1 to Q4, and can also be represented using {@link QuarterOfYear}.
     */
    QUARTER_OF_YEAR("QuarterOfYear", QUARTER_YEARS, YEARS,  DateTimeValueRange.of(1, 4)) {
        @Override
        public DateTimeValueRange doRange(DateTimeAccessor dateTime) {
            return range();
        }
        @Override
        public long doGet(DateTimeAccessor dateTime) {
            return ((dateTime.getLong(MONTH_OF_YEAR) - 1) / 3) + 1;
        }
        @Override
        public <R extends DateTimeAccessor> R doSet(R dateTime, long newValue) {
            long curValue = doGet(dateTime);
            range().checkValidValue(newValue, this);
            return (R) dateTime.with(MONTH_OF_YEAR, dateTime.getLong(MONTH_OF_YEAR) + (newValue - curValue) * 3);
        }
    };

    private static final DateTimeValueRange RANGE_DOQ_90_91 = DateTimeValueRange.of(1, 90, 91);
    private static final DateTimeValueRange RANGE_DOQ_90 = DateTimeValueRange.of(1, 90);
    private static final DateTimeValueRange RANGE_DOQ_91 = DateTimeValueRange.of(1, 91);
    private static final DateTimeValueRange RANGE_DOQ_92 = DateTimeValueRange.of(1, 92);
    private static final int[] QUARTER_DAYS = {0, 90, 181, 273, 0, 91, 182, 274};

    private final String name;
    private final PeriodUnit baseUnit;
    private final PeriodUnit rangeUnit;
    private final DateTimeValueRange range;

    private QuarterYearField(String name, PeriodUnit baseUnit, PeriodUnit rangeUnit, DateTimeValueRange range) {
        this.name = name;
        this.baseUnit = baseUnit;
        this.rangeUnit = rangeUnit;
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
    public DateTimeValueRange range() {
        return range;
    }

    @Override
    public boolean isSupported(DateTimeAccessor dateTime) {
        if (dateTime instanceof LocalDate || dateTime instanceof LocalDateTime ||
                dateTime instanceof OffsetDateTime || dateTime instanceof ZonedDateTime) {
            return true;
        } else if (dateTime instanceof LocalTime || dateTime instanceof Instant) {
            return false;
        }
        try {
            dateTime.getLong(this);
            return true;
        } catch (RuntimeException ex) {
            return false;
        }
    }

    @Override
    public int compare(DateTimeAccessor dateTime1, DateTimeAccessor dateTime2) {
        return Long.compare(dateTime1.getLong(this), dateTime2.getLong(this));
    }

    //-----------------------------------------------------------------------
    @Override
    public boolean resolve(DateTimeBuilder builder, long value) {
        Long[] values = builder.queryFieldValues(DAY_OF_QUARTER, MONTH_OF_QUARTER, QUARTER_OF_YEAR, MONTH_OF_YEAR);
        Long doqLong = values[0];
        Long moqLong = values[1];
        Long qoyLong = values[2];
        Long moyLong = values[3];
        // expand moy to ensure that moq+moy and qoy_moy combinations are validated
        if (moqLong == null && moyLong != null) {
            moqLong = ((moyLong - 1) % 3) + 1;
        }
        if (qoyLong == null && moyLong != null) {
            qoyLong = ((moyLong - 1) / 3) + 1;
        }
        // doq+qoy (before moq+qoy)
        if (doqLong != null && qoyLong != null) {
            int qoy = DateTimes.safeToInt(qoyLong);
            long doq = doqLong;
            if (qoy == 1) {
                builder.addFieldValue(DAY_OF_YEAR, doq);
            } else {
                Month month = QuarterOfYear.of(qoy).firstMonth();
                int len = month.length(false);
                if (doq > len) {
                    month = month.plus(1);
                    doq = doq - len;
                    len = month.length(false);
                    if (doq > len) {
                        month = month.plus(1);
                        doq = doq - len;
                    }
                }
                builder.addFieldValue(DAY_OF_MONTH, doq);
                builder.addFieldValue(MONTH_OF_YEAR, month.getValue());
            }
            builder.removeFieldValues(DAY_OF_QUARTER, QUARTER_OF_YEAR);
            return true;
        }
        // moq+qoy
        if (moqLong != null && qoyLong != null) {
            long calcMoy = ((qoyLong - 1) * 3) + moqLong;
            builder.addFieldValue(MONTH_OF_YEAR, calcMoy);
            builder.removeFieldValues(MONTH_OF_QUARTER, QUARTER_OF_YEAR);
            return true;
        }
        return false;
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
        return getName();
    }

}
