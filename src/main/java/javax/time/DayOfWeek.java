/*
 * Copyright (c) 2007-2012, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time;

import static javax.time.calendrical.LocalDateTimeField.DAY_OF_WEEK;
import static javax.time.calendrical.LocalPeriodUnit.DAYS;
import static javax.time.calendrical.LocalPeriodUnit.WEEKS;

import java.util.Locale;

import javax.time.calendrical.AdjustableDateTime;
import javax.time.calendrical.DateTimeAccessor;
import javax.time.calendrical.DateTimeAdjuster;
import javax.time.calendrical.DateTimeField;
import javax.time.calendrical.DateTimeValueRange;
import javax.time.calendrical.LocalDateTimeField;
import javax.time.calendrical.LocalPeriodUnit;
import javax.time.calendrical.PeriodUnit;
import javax.time.format.DateTimeFormatterBuilder;
import javax.time.format.TextStyle;

/**
 * A day-of-week, such as 'Tuesday'.
 * <p>
 * {@code DayOfWeek} is an enum representing the 7 days of the week -
 * Monday, Tuesday, Wednesday, Thursday, Friday, Saturday and Sunday.
 * <p>
 * All date-time fields have an {@code int} value.
 * The {@code int} value follows the ISO-8601 standard, from 1 (Monday) to 7 (Sunday).
 * It is recommended that applications use the enum rather than the {@code int} value
 * to ensure code clarity.
 * <p>
 * This enum provides access to the localized textual form of the day-of-week.
 * Some locales also assign different numeric values to the days, declaring Sunday to have
 * the value 1. This class provides no support for localized numbering.
 * <p>
 * <b>Do not use {@code ordinal()} to obtain the numeric representation of {@code DayOfWeek}.
 * Use {@code getValue()} instead.</b>
 * <p>
 * This enum represents a common concept that is found in many calendar systems.
 * As such, this enum may be used by any calendar system that has the day-of-week
 * concept defined exactly equivalent to the ISO calendar system.
 * 
 * <h4>Implementation notes</h4>
 * This is an immutable and thread-safe enum.
 */
public enum DayOfWeek implements AdjustableDateTime, DateTimeAdjuster {

    /**
     * The singleton instance for the day-of-week of Monday.
     * This has the numeric value of {@code 1}.
     */
    MONDAY,
    /**
     * The singleton instance for the day-of-week of Tuesday.
     * This has the numeric value of {@code 2}.
     */
    TUESDAY,
    /**
     * The singleton instance for the day-of-week of Wednesday.
     * This has the numeric value of {@code 3}.
     */
    WEDNESDAY,
    /**
     * The singleton instance for the day-of-week of Thursday.
     * This has the numeric value of {@code 4}.
     */
    THURSDAY,
    /**
     * The singleton instance for the day-of-week of Friday.
     * This has the numeric value of {@code 5}.
     */
    FRIDAY,
    /**
     * The singleton instance for the day-of-week of Saturday.
     * This has the numeric value of {@code 6}.
     */
    SATURDAY,
    /**
     * The singleton instance for the day-of-week of Sunday.
     * This has the numeric value of {@code 7}.
     */
    SUNDAY;
    /**
     * Private cache of all the constants.
     */
    private static final DayOfWeek[] ENUMS = DayOfWeek.values();

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code DayOfWeek} from an {@code int} value.
     * <p>
     * {@code DayOfWeek} is an enum representing the 7 days of the week.
     * This factory allows the enum to be obtained from the {@code int} value.
     * The {@code int} value follows the ISO-8601 standard, from 1 (Monday) to 7 (Sunday).
     * <p>
     * An exception is thrown if the value is invalid.
     *
     * @param dayOfWeek  the day-of-week to represent, from 1 (Monday) to 7 (Sunday)
     * @return the DayOfWeek singleton, not null
     * @throws DateTimeException if the day-of-week is invalid
     */
    public static DayOfWeek of(int dayOfWeek) {
        if (dayOfWeek < 1 || dayOfWeek > 7) {
            throw new DateTimeException("Invalid value for DayOfWeek: " + dayOfWeek);
        }
        return ENUMS[dayOfWeek - 1];
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code DayOfWeek} from a calendrical.
     * <p>
     * A calendrical represents some form of date and time information.
     * This factory converts the arbitrary calendrical to an instance of {@code DayOfWeek}.
     * 
     * @param calendrical  the calendrical to convert, not null
     * @return the day-of-week, not null
     * @throws DateTimeException if unable to convert to a {@code DayOfWeek}
     */
    public static DayOfWeek from(DateTimeAccessor calendrical) {
        if (calendrical instanceof DayOfWeek) {
            return (DayOfWeek) calendrical;
        }
        return of((int) calendrical.get(DAY_OF_WEEK));
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the day-of-week {@code int} value.
     * <p>
     * The values are numbered following the ISO-8601 standard,
     * from 1 (Monday) to 7 (Sunday).
     *
     * @return the day-of-week, from 1 (Monday) to 7 (Sunday)
     */
    public int getValue() {
        return ordinal() + 1;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the textual representation, such as 'Mon' or 'Friday'.
     * <p>
     * This returns the textual name used to identify the day-of-week.
     * The parameters control the length of the returned text and the locale.
     * <p>
     * If no textual mapping is found then the {@link #getValue() numeric value} is returned.
     *
     * @param style  the length of the text required, not null
     * @param locale  the locale to use, not null
     * @return the text value of the day-of-week, not null
     */
    public String getText(TextStyle style, Locale locale) {
        return new DateTimeFormatterBuilder().appendText(DAY_OF_WEEK, style).toFormatter(locale).print(this);
    }

    //-----------------------------------------------------------------------
    @Override
    public DateTimeValueRange range(DateTimeField field) {
        if (field instanceof LocalDateTimeField) {
            return field.range();
        }
        return field.doRange(this);
    }

    @Override
    public long get(DateTimeField field) {
        if (field == DAY_OF_WEEK) {
            return getValue();
        } else if (field instanceof LocalDateTimeField) {
            throw new DateTimeException("Unsupported field: " + field.getName());
        }
        return field.doGet(this);
    }

    @Override
    public DayOfWeek with(DateTimeField field, long newValue) {
        if (field == DAY_OF_WEEK) {
            ((LocalDateTimeField) field).checkValidValue(newValue);
            return DayOfWeek.of((int) newValue);
        } else if (field instanceof LocalDateTimeField) {
            throw new DateTimeException("Unsupported field: " + field.getName());
        }
        return field.doSet(this, newValue);
    }

    //-----------------------------------------------------------------------
    @Override
    public DayOfWeek plus(long periodAmount, PeriodUnit unit) {
        if (unit == DAYS) {
            return plus(periodAmount);
        } else if (unit == WEEKS) {
            return this;
        } else if (unit instanceof LocalPeriodUnit) {
            throw new DateTimeException("Unsupported unit: " + unit.getName());
        }
        return unit.doAdd(this, periodAmount);
    }

    /**
     * Returns the day-of-week that is the specified number of days after this one.
     * <p>
     * The calculation rolls around the end of the week from Sunday to Monday.
     * The specified period may be negative.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param days  the days to add, positive or negative
     * @return the resulting day-of-week, not null
     */
    public DayOfWeek plus(long days) {
        int amount = (int) (days % 7);
        return values()[(ordinal() + (amount + 7)) % 7];
    }

    //-----------------------------------------------------------------------
    @Override
    public DayOfWeek minus(long periodAmount, PeriodUnit unit) {
        if (unit instanceof LocalPeriodUnit) {
            return plus(-(periodAmount % 7), unit);
        }
        return unit.doAdd(this, DateTimes.safeNegate(periodAmount));
    }

    /**
     * Returns the day-of-week that is the specified number of days before this one.
     * <p>
     * The calculation rolls around the start of the year from Monday to Sunday.
     * The specified period may be negative.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param days  the days to subtract, positive or negative
     * @return the resulting day-of-week, not null
     */
    public DayOfWeek minus(long days) {
        return plus(-(days % 7));
    }

    //-----------------------------------------------------------------------
    /**
     * Extracts date-time information in a generic way.
     * <p>
     * This method exists to fulfill the {@link DateTimeAccessor} interface.
     * This implementation always returns null.
     * 
     * @param <R> the type to extract
     * @param type  the type to extract, null returns null
     * @return the extracted object, null if unable to extract
     */
    @Override
    public <R> R extract(Class<R> type) {
        return null;
    }

    /**
     * Implementation of the strategy to make an adjustment to the specified date-time object.
     * <p>
     * This method is not intended to be called by application code directly.
     * Applications should use the {@code with(DateTimeAdjuster)} method on the
     * date-time object to make the adjustment passing this as the argument.
     * <p>
     * This instance is immutable and unaffected by this method call.
     * 
     * <h4>Implementation notes</h4>
     * Adjusts the specified date-time to have the value of this day-of-week.
     * Note that this adjusts forwards or backwards within a Monday to Sunday week.
     * The adjustment is equivalent to using {@link AdjustableDateTime#with(DateTimeField, long)}
     * passing {@code DAY_OF_WEEK} as the field.
     *
     * @param dateTime  the target object to be adjusted, not null
     * @return the adjusted object, not null
     */
    @Override
    public AdjustableDateTime doAdjustment(AdjustableDateTime dateTime) {
        return dateTime.with(DAY_OF_WEEK, getValue());
    }

}
