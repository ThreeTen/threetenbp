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
import javax.time.MonthOfYear;

/**
 * A standard set of {@code LocalDate} fields.
 * <p>
 * This set of fields provide framework-level access to manipulate a {@code LocalDate}.
 * 
 * <h4>Implementation notes</h4>
 * This is a final, immutable and thread-safe enum.
 * 
 * @see LocalTimeField
 */
public enum LocalDateField implements DateTimeField {

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

    private LocalDateField(String name, PeriodUnit baseUnit, PeriodUnit rangeUnit) {
        this.name = name;
        this.baseUnit = baseUnit;
        this.rangeUnit = rangeUnit;
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
        throw new IllegalStateException("Unreachable");
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
            switch (this) {
                case DAY_OF_MONTH: return DateTimeValueRange.of(1, date.getMonthOfYear().lengthInDays(date.isLeapYear()));
                case DAY_OF_YEAR: return DateTimeValueRange.of(1, date.isLeapYear() ? 366 : 365);
                case ALIGNED_WEEK_OF_MONTH: return DateTimeValueRange.of(1,
                            date.getMonthOfYear() == MonthOfYear.FEBRUARY && date.isLeapYear() == false ? 4 : 5);
            }
            return getValueRange();
        }
        throw new CalendricalException(this + " not valid for " + calendrical);
    }

    @Override
    public long get(CalendricalObject calendrical) {
        LocalDate date = calendrical.extract(LocalDate.class);
        if (date != null) {
            switch (this) {
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
            throw new IllegalStateException("Unreachable");
        }

        DateTimeBuilder builder = calendrical.extract(DateTimeBuilder.class);
        if (builder.containsFieldValue(this)) {
            return builder.getFieldValue(this);
        }
        throw new CalendricalException(this + " not valid for " + calendrical);
    }

    //-----------------------------------------------------------------------
    @Override
    public <R extends CalendricalObject> R set(R calendrical, long newValue) {
        LocalDate date = calendrical.extract(LocalDate.class);
        if (date == null) {
            throw new CalendricalException(this + " not valid for " + calendrical);
        }

        if (range(date).isValidValue(newValue) == false) {
            throw new CalendricalException("Invalid value: " + name + " " + newValue);
        }
        switch (this) {
            case DAY_OF_WEEK:
                date = date.plusDays(newValue - date.getDayOfWeek().getValue());
                break;
            case ALIGNED_DAY_OF_WEEK_IN_MONTH:
            case ALIGNED_DAY_OF_WEEK_IN_YEAR:
                date = date.plusDays(newValue - get(date));
                break;
            case DAY_OF_MONTH:
                date = date.withDayOfMonth((int)newValue);
                break;
            case DAY_OF_YEAR:
                date = date.withDayOfYear((int)newValue);
                break;
            case EPOCH_DAY:
                date = LocalDate.ofEpochDay(newValue);
                break;
            case ALIGNED_WEEK_OF_MONTH:
            case ALIGNED_WEEK_OF_YEAR:
                date = date.plusWeeks(newValue - get(date));
                break;
            case MONTH_OF_YEAR:
                date = date.withMonthOfYear((int)newValue);
                break;
            case EPOCH_MONTH:
                date = date.plusMonths(newValue - get(date));
                break;
            case YEAR:
                date = date.withYear((int) newValue);
                break;
            default:
                throw new IllegalStateException("Unreachable");
        }
        return (R)calendrical.with(date);
    }

    //-----------------------------------------------------------------------
    @Override    
    public <R extends CalendricalObject> R roll(R value, long newValue) {
        LocalDate date = value.extract(LocalDate.class);
        if (date == null) {
            // TBD: all through doing nothing to ignore the change in any other type.
            return value;
        }
        switch (this) {
            case DAY_OF_WEEK: 
                date = date.plusDays(newValue);
                break;
            case DAY_OF_MONTH: 
                date =  date.plusDays(newValue);
                break;
            case DAY_OF_YEAR: 
                date = date.plusDays(newValue);
                break;
            case EPOCH_DAY: 
                date = LocalDate.ofEpochDay(newValue);
                break;
            case MONTH_OF_YEAR: 
                date = date.plusMonths(newValue);
                break;
            case EPOCH_MONTH: 
                date = date.plusMonths(newValue);
                break;
            case YEAR: 
                date = date.plusYears(newValue);
                break;
            default:
                throw new IllegalStateException("Unreachable");
        }
        return (R)value.with(date);
    }

    //-----------------------------------------------------------------------
   @Override
    public boolean resolve(DateTimeBuilder builder, long value) {
        return false;  // resolve implemented in builder
    }
  
    //-----------------------------------------------------------------------
    /**
     * Checks that the specified value is valid for this field.
     * <p>
     * This validates that the value is within the outer range of valid values
     * returned by {@link #getValueRange()}.
     * 
     * @param value  the value to check
     * @return the value that was passed in
     */
    public long checkValidValue(long value) {  // JAVA8 default method on interface
        return getValueRange().checkValidValue(value, this);
    }

    /**
     * Checks that the specified value is valid and fits in an {@code int}.
     * <p>
     * This validates that the value is within the outer range of valid values
     * returned by {@link #getValueRange()}.
     * It also checks that all valid values are within the bounds of an {@code int}.
     * 
     * @param value  the value to check
     * @return the value that was passed in
     */
    public int checkValidIntValue(long value) {  // JAVA8 default method on interface
        return getValueRange().checkValidIntValue(value, this);
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
        return getName();
    }

}
