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
package javax.time.calendrical;

import static javax.time.calendrical.LocalDateUnit.DAYS;
import static javax.time.calendrical.LocalDateUnit.FOREVER;
import static javax.time.calendrical.LocalDateUnit.MONTHS;
import static javax.time.calendrical.LocalDateUnit.WEEKS;
import static javax.time.calendrical.LocalDateUnit.YEARS;

import javax.time.CalendricalException;
import javax.time.DateTimes;
import javax.time.LocalDate;
import javax.time.LocalDateTime;
import javax.time.MonthOfYear;

/**
 * A standard set of {@code LocalDate} fields.
 * <p>
 * This set of fields provide framework-level access to manipulate a {@code LocalDate}.
 * <p>
 * This is a final, immutable and thread-safe enum.
 * 
 * @see LocalTimeField
 */
public enum LocalDateField implements DateField {

    /**
     * The day-of-week, such as Tuesday.
     * The days are numbered from Monday (1) to Sunday (7) as per ISO-8601.
     */
    DAY_OF_WEEK("DayOfWeek", DAYS, WEEKS),
    /**
     * The aligned day-of-week within a month.
     * The value of this field is based on 7 day weeks where the first day of the
     * week is the first day of a given month.
     * Thus, day-of-month 1 to 7 will have aligned-day-of-week values from 1 to 7.
     * From day-of-month 8 to 14 the aligned-day-of-week values will again be from 1 to 7, and so on.
     * This is typically used with {@code ALIGNED_WEEK_OF_MONTH}.
     */
    ALIGNED_DAY_OF_WEEK_IN_MONTH("AlignedDayOfWeekInMonth", DAYS, WEEKS),
    /**
     * The aligned day-of-week within a year.
     * The value of this field is based on 7 day weeks where the first day of the
     * week is the first day of a given year.
     * Thus, day-of-year 1 to 7 will have aligned-day-of-week values from 1 to 7.
     * From day-of-year 8 to 14 the aligned-day-of-week values will again be from 1 to 7, and so on.
     * This is used with {@code ALIGNED_WEEK_OF_MONTH}.
     */
    ALIGNED_DAY_OF_WEEK_IN_YEAR("AlignedDayOfWeekInYear", DAYS, WEEKS),
    /**
     * The day-of-month.
     * The days are numbered from 1 to 31 in most months as per ISO-8601.
     * April, June, September, November have days from 1 to 30.
     * February has days from 1 to 28, or 29 in a leap year.
     */
    DAY_OF_MONTH("DayOfMonth", DAYS, MONTHS),
    /**
     * The day-of-year.
     * The days are numbered from 1 to 365 in standard years and 1 to 366 in leap years as per ISO-8601.
     */
    DAY_OF_YEAR("DayOfYear", DAYS, YEARS),
    /**
     * The epoch day based on the Java epoch of 1970-01-01.
     * The value is a sequential count of days where 1970-01-01 is zero.
     */
    EPOCH_DAY("EpochDay", DAYS, FOREVER),
    /**
     * The aligned week within a month.
     * The value of this field is a count of 7 day weeks within a month where the
     * first week starts on the first day of a given month.
     * Thus, day-of-month values 1 to 7 are in aligned-week 1, while day-of-month values
     * 8 to 14 are in week 2, and so on.
     * This is typically used with {@code ALIGNED_DAY_OF_WEEK_IN_MONTH}.
     */
    ALIGNED_WEEK_OF_MONTH("AlignedWeekOfMonth", WEEKS, MONTHS),
    /**
     * The aligned week within a year.
     * The value of this field is a count of 7 day weeks within a month where the
     * first week starts on the first day of a given year.
     * Thus, day-of-year values 1 to 7 are in aligned-week 1, while day-of-year values
     * 8 to 14 are in week 2, and so on.
     * This is typically used with {@code ALIGNED_DAY_OF_WEEK_IN_YEAR}.
     */
    ALIGNED_WEEK_OF_YEAR("AlignedWeekOfYear", WEEKS, YEARS),
    /**
     * The month-of-year, such as March.
     * The months are numbered from 1 to 12 as per ISO-8601.
     */
    MONTH_OF_YEAR("MonthOfYear", MONTHS, YEARS),
    /**
     * The epoch month based on the Java epoch of 1970-01-01.
     * The value is a sequential count of months where January 1970 is zero.
     */
    EPOCH_MONTH("EpochMonth", MONTHS, FOREVER),
    /**
     * The year, such as 2012.
     * The year defined as per ISO-8601.
     */
    YEAR("Year", YEARS, FOREVER);

    private static final long MIN_EPOCH_MONTH = (DateTimes.MIN_YEAR - 1970L) * 12L;
    private static final long MAX_EPOCH_MONTH = (DateTimes.MAX_YEAR - 1970L) * 12L - 1L;
    private static final long MIN_EPOCH_DAY = (long) (DateTimes.MIN_YEAR * 365.25);
    private static final long MAX_EPOCH_DAY = (long) (DateTimes.MAX_YEAR * 365.25);
    private static final DateTimeValueRange RANGE_DOW = DateTimeValueRange.of(1, 7);
    private static final DateTimeValueRange RANGE_DOM = DateTimeValueRange.of(1, 28, 31);
    private static final DateTimeValueRange RANGE_DOY = DateTimeValueRange.of(1, 365, 366);
    private static final DateTimeValueRange RANGE_ED = DateTimeValueRange.of(MIN_EPOCH_DAY, MAX_EPOCH_DAY);
    private static final DateTimeValueRange RANGE_MOY = DateTimeValueRange.of(1, 12);
    private static final DateTimeValueRange RANGE_EM = DateTimeValueRange.of(MIN_EPOCH_MONTH, MAX_EPOCH_MONTH);
    private static final DateTimeValueRange RANGE_Y = DateTimeValueRange.of(DateTimes.MIN_YEAR, DateTimes.MAX_YEAR);

    private final String name;
    private final PeriodUnit baseUnit;
    private final PeriodUnit rangeUnit;
    private final Rules<LocalDate> dRules;
    private final Rules<LocalDateTime> dtRules;

    private LocalDateField(String name, PeriodUnit baseUnit, PeriodUnit rangeUnit) {
        this.name = name;
        this.baseUnit = baseUnit;
        this.rangeUnit = rangeUnit;
        this.dRules = new DRules(this);
        this.dtRules = DateTimes.rulesForDate(this.dRules);
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
    public Rules<LocalDate> getDateRules() {
        return dRules;
    }

    @Override
    public Rules<LocalDateTime> getDateTimeRules() {
        return dtRules;
    }

    @Override
    public DateTimeValueRange getValueRange() {
        switch (this) {
            case DAY_OF_WEEK: return RANGE_DOW;
            case ALIGNED_DAY_OF_WEEK_IN_MONTH: return RANGE_DOW;
            case ALIGNED_DAY_OF_WEEK_IN_YEAR: return RANGE_DOW;
            case DAY_OF_MONTH: return RANGE_DOM;
            case DAY_OF_YEAR: return RANGE_DOY;
            case ALIGNED_WEEK_OF_MONTH: return DateTimeValueRange.of(1, 4, 5);
            case ALIGNED_WEEK_OF_YEAR: return DateTimeValueRange.of(1, 53);
            case EPOCH_DAY: return RANGE_ED;
            case MONTH_OF_YEAR: return RANGE_MOY;
            case EPOCH_MONTH: return RANGE_EM;
            case YEAR: return RANGE_Y;
        }
        throw new CalendricalException("Unknown field");
    }

    @Override
    public long getValueFrom(CalendricalObject calendrical) {
        return getDateRules().get(LocalDate.from(calendrical));
    }

    public void checkValidValue(long value) {
        getValueRange().checkValidValue(value, this);
    }

    public int checkValidIntValue(long value) {
        return getValueRange().checkValidIntValue(value, this);
    }

    @Override
    public String toString() {
        return getName();
    }

    //-------------------------------------------------------------------------
    /**
     * Date rules for the field.
     */
    private static final class DRules implements Rules<LocalDate> {
        private final LocalDateField field;
        private DRules(LocalDateField field) {
            this.field = field;
        }
        @Override
        public DateTimeValueRange range(LocalDate date) {
            switch (field) {
                case DAY_OF_MONTH: return DateTimeValueRange.of(1, date.getMonthOfYear().lengthInDays(date.isLeapYear()));
                case DAY_OF_YEAR: return DateTimeValueRange.of(1, date.isLeapYear() ? 366 : 365);
                case ALIGNED_WEEK_OF_MONTH: return DateTimeValueRange.of(1, 
                                date.getMonthOfYear() == MonthOfYear.FEBRUARY && date.isLeapYear() == false ? 4 : 5);
            }
            return field.getValueRange();
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
        public LocalDate roll(LocalDate date, long roll) {
            return null;  // TODO
//            DateTimeRuleRange range = range(date);
//            long valueRange = (range.getMaximum() - range.getMinimum()) + 1;
//            long currentValue = get(date);
//            long newValue = roll % valueRange; // TODO
//            return addToDate(date, field.getBaseUnit(), newValue - currentValue);
        }
        @Override
        public boolean resolve(DateTimeBuilder builder, long value) {
            return false;  // resolve implemented in builder
        }
    }

}
