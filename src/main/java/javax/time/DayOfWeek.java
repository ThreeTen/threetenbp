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
import static javax.time.calendrical.LocalDateTimeUnit.DAYS;

import javax.time.calendrical.CalendricalAdjuster;
import javax.time.calendrical.CalendricalObject;
import javax.time.calendrical.DateTimeBuilder;
import javax.time.calendrical.DateTimeField;
import javax.time.calendrical.DateTimeObject;
import javax.time.calendrical.LocalDateTimeField;
import javax.time.calendrical.LocalDateTimeUnit;
import javax.time.calendrical.PeriodUnit;

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
public enum DayOfWeek implements DateTimeObject {
//    * <p>
//    * This enum provides access to the localized textual form of the day-of-week.
//    * However, some countries assign different numeric values to the days, such as Sunday = 1.
//    * Applications requiring such a localized numbering scheme should use {@link WeekRules}.

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
     * @throws CalendricalException if the day-of-week is invalid
     */
    public static DayOfWeek of(int dayOfWeek) {
        if (dayOfWeek < 1 || dayOfWeek > 7) {
            throw new CalendricalException("Invalid value for DayOfWeek: " + dayOfWeek);
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
     * @throws CalendricalException if unable to convert to a {@code DayOfWeek}
     */
    public static DayOfWeek from(CalendricalObject calendrical) {
        if (calendrical instanceof DayOfWeek) {
            return (DayOfWeek) calendrical;
        }
        return of((int) DAY_OF_WEEK.get(calendrical));
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
     * This enum uses the {@link ISODateTimeRule#DAY_OF_WEEK} rule to obtain the text.
     * This allows the text to be localized by language, but not by chronology.
     * <p>
     * If no textual mapping is found then the {@link #getValue() numeric value} is returned.
     *
     * @param locale  the locale to use, not null
     * @return the short text value of the day-of-week, not null
     */
//    public String getText(TextStyle style, Locale locale) {
//        return DAY_OF_WEEK.getText(getValue(), style, locale);
//    }

    //-----------------------------------------------------------------------
    /**
     * Gets the next day-of-week.
     * <p>
     * This calculates based on the time-line, thus it rolls around the end of
     * the week. The next day after Sunday is Monday.
     *
     * @return the next day-of-week, not null
     */
    public DayOfWeek next() {
        return roll(1);
    }

    /**
     * Gets the previous day-of-week.
     * <p>
     * This calculates based on the time-line, thus it rolls around the end of
     * the week. The previous day before Monday is Sunday.
     *
     * @return the previous day-of-week, not null
     */
    public DayOfWeek previous() {
        return roll(-1);
    }

    /**
     * Rolls the day-of-week, adding the specified number of days.
     * <p>
     * This calculates based on the time-line, thus it rolls around the end of
     * the week from Sunday to Monday. The days to roll by may be negative.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param days  the days to roll by, positive or negative
     * @return the resulting day-of-week, not null
     */
    public DayOfWeek roll(int days) {
        return values()[(ordinal() + (days % 7 + 7)) % 7];
    }

    //-----------------------------------------------------------------------
    /**
     * Extracts date-time information in a generic way.
     * <p>
     * This method exists to fulfill the {@link CalendricalObject} interface.
     * This implementation returns the following types:
     * <ul>
     * <li>AmPmOfDay
     * <li>DateTimeBuilder, using {@link LocalDateTimeField#DAY_OF_WEEK}
     * <li>Class, returning {@code DayOfWeek}
     * </ul>
     * 
     * @param <R> the type to extract
     * @param type  the type to extract, null returns null
     * @return the extracted object, null if unable to extract
     */
    @SuppressWarnings("unchecked")
    @Override
    public <R> R extract(Class<R> type) {
        if (type == DateTimeBuilder.class) {
            return (R) new DateTimeBuilder(DAY_OF_WEEK, getValue());
        } else if (type == Class.class) {
            return (R) DayOfWeek.class;
        } else if (type == DayOfWeek.class) {
            return (R) this;
        }
        return null;
    }

    @Override
    public DayOfWeek with(CalendricalAdjuster adjuster) {
        if (adjuster instanceof DayOfWeek) {
            return ((DayOfWeek) adjuster);
        }
        DateTimes.checkNotNull(adjuster, "Adjuster must not be null");
        throw new CalendricalException("Unable to adjust DayOfWeek with " + adjuster.getClass().getSimpleName());
    }

    //-----------------------------------------------------------------------
    @Override
    public long get(DateTimeField field) {
        if (field == DAY_OF_WEEK) {
            return getValue();
        } else if (field instanceof LocalDateTimeField) {
            throw new CalendricalException(field.getName() + " not valid for DayOfWeek");
        }
        return field.get(this);
    }

    @Override
    public DayOfWeek with(DateTimeField field, long newValue) {
        if (field == DAY_OF_WEEK) {
            ((LocalDateTimeField) field).checkValidValue(newValue);
            return DayOfWeek.of((int) newValue);
        } else if (field instanceof LocalDateTimeField) {
            throw new CalendricalException(field.getName() + " not valid for DayOfWeek");
        }
        return field.set(this, newValue);
    }

    @Override
    public DayOfWeek plus(long period, PeriodUnit unit) {
        if (unit == DAYS) {
            return roll((int) (period % 7));  // TODO roll should take a long
        } else if (unit instanceof LocalDateTimeUnit) {
            throw new CalendricalException(unit.getName() + " not valid for DayOfWeek");
        }
        return unit.add(this, period);
    }

    @Override
    public DayOfWeek minus(long period, PeriodUnit unit) {
        return plus(DateTimes.safeNegate(period), unit);
    }

}
