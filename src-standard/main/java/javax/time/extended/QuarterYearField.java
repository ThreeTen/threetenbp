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
package javax.time.extended;

import static javax.time.calendrical.LocalDateTimeField.DAY_OF_MONTH;
import static javax.time.calendrical.LocalDateTimeField.DAY_OF_YEAR;
import static javax.time.calendrical.LocalDateTimeField.MONTH_OF_YEAR;
import static javax.time.calendrical.LocalDateTimeUnit.DAYS;
import static javax.time.calendrical.LocalDateTimeUnit.MONTHS;
import static javax.time.calendrical.LocalDateTimeUnit.QUARTER_YEARS;
import static javax.time.calendrical.LocalDateTimeUnit.YEARS;

import javax.time.CalendricalException;
import javax.time.DateTimes;
import javax.time.LocalDate;
import javax.time.Month;
import javax.time.calendrical.CalendricalObject;
import javax.time.calendrical.DateTimeBuilder;
import javax.time.calendrical.DateTimeCalendrical;
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

    DAY_OF_QUARTER("DayOfQuarter", DAYS, QUARTER_YEARS, DateTimeValueRange.of(1, 90, 92)),
    MONTH_OF_QUARTER("MonthOfQuarter", MONTHS, QUARTER_YEARS,  DateTimeValueRange.of(1, 3)),
    QUARTER_OF_YEAR("QuarterOfYear", QUARTER_YEARS, YEARS,  DateTimeValueRange.of(1, 4));

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
    public DateTimeValueRange getValueRange() {
        return range;
    }

    @Override
    public int compare(CalendricalObject calendrical1, CalendricalObject calendrical2) {
        return DateTimes.safeCompare(get(calendrical1), get(calendrical2));
    }

    //-----------------------------------------------------------------------
    @Override
    public DateTimeValueRange range(CalendricalObject calendrical) {
        LocalDate date = calendrical.extract(LocalDate.class);
        if (date != null) {
            if (this == DAY_OF_QUARTER) {
                switch (date.getMonth().ordinal() / 3) {
                    case 0: return (date.isLeapYear() ? RANGE_DOQ_91 : RANGE_DOQ_90);
                    case 1: return RANGE_DOQ_91;
                    case 2: return RANGE_DOQ_92;
                    case 3: return RANGE_DOQ_92;
                }
            }
            return getValueRange();
        }
        throw new CalendricalException("Unable to obtain " + getName() + " from calendrical: " + calendrical.getClass());
    }

    @Override
    public long get(CalendricalObject calendrical) {
        LocalDate date = calendrical.extract(LocalDate.class);
        if (date != null) {
            switch (this) {
                case DAY_OF_QUARTER: return doq(date);
                case MONTH_OF_QUARTER: return (date.getMonth().ordinal() % 3) + 1;
                case QUARTER_OF_YEAR: return (date.getMonth().ordinal() / 3) + 1;
            }
            throw new IllegalStateException("Unreachable");
        }
        DateTimeBuilder builder = calendrical.extract(DateTimeBuilder.class);
        if (builder.containsFieldValue(this)) {
            return builder.getFieldValue(this);
        }
//        if (calendrical instanceof DateTimeCalendrical) {
//            long moy0 = ((DateTimeCalendrical) calendrical).get(MONTH_OF_YEAR) - 1;
//            switch (this) {
//                case MONTH_OF_QUARTER: return DateTimes.floorMod(moy0, 3) + 1;
//                case QUARTER_OF_YEAR: return DateTimes.floorDiv(moy0, 3) + 1;
//            }
//        }
        throw new CalendricalException("Unable to obtain " + getName() + " from calendrical: " + calendrical.getClass());
    }

    @Override
    public <R extends CalendricalObject> R set(R calendrical, long newValue) {
        LocalDate date = calendrical.extract(LocalDate.class);
        if (date != null) {
            if (range(date).isValidValue(newValue) == false) {
                throw new CalendricalException("Invalid value: " + name + " " + newValue);
            }
            long value0 = newValue - 1;
            switch (this) {
                case DAY_OF_QUARTER: date = date.plusDays(value0 - (doq(date) - 1));
                    break;
                case MONTH_OF_QUARTER: date = date.plusMonths(value0 - (date.getMonth().ordinal() % 3));
                    break;
                case QUARTER_OF_YEAR: date = date.plusMonths((value0 - (date.getMonth().ordinal() / 3)) * 3);
                    break;
                default:
                    throw new IllegalStateException("Unreachable");
            }
            return (R) calendrical.with(date);
        }
        throw new CalendricalException("Unable to obtain " + getName() + " from calendrical: " + calendrical.getClass());
    }

    @Override
    public <R extends CalendricalObject> R roll(R calendrical, long roll) {
        LocalDate date = calendrical.extract(LocalDate.class);
        if (date != null) {
            DateTimeValueRange newrange = range(date);
            long valueRange = (newrange.getMaximum() - newrange.getMinimum()) + 1;
            long curValue0 = get(date) - 1;
            long newValue = ((curValue0 + (roll % valueRange)) % valueRange) + 1;
            return set(calendrical, newValue);
        }
        throw new CalendricalException("Unable to obtain " + getName() + " from calendrical: " + calendrical.getClass());
    }

    private static int doq(LocalDate date) {
        return date.getDayOfYear() - QUARTER_DAYS[(date.getMonth().ordinal() / 3) + (date.isLeapYear() ? 4 : 0)];
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
                Month month = QuarterOfYear.of(qoy).getFirstMonthOfQuarter();
                int len = month.length(false);
                if (doq > len) {
                    month = month.next();
                    doq = doq - len;
                    len = month.length(false);
                    if (doq > len) {
                        month = month.next();
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
