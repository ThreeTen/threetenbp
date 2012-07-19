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

import static javax.time.calendrical.LocalDateTimeField.AMPM_OF_DAY;
import static javax.time.calendrical.LocalDateTimeField.HOUR_OF_DAY;

import java.util.Calendar;
import java.util.Locale;

import javax.time.calendrical.AdjustableDateTime;
import javax.time.calendrical.DateTime;
import javax.time.calendrical.DateTimeAdjuster;
import javax.time.calendrical.DateTimeBuilder;
import javax.time.calendrical.DateTimeField;
import javax.time.calendrical.LocalDateTimeField;
import javax.time.calendrical.LocalPeriodUnit;
import javax.time.calendrical.PeriodUnit;
import javax.time.format.TextStyle;

/**
 * A half-day before or after midday, with the values 'AM' and 'PM'.
 * <p>
 * {@code AmPm} is an enum representing the half-day concepts of AM and PM.
 * AM is defined as from 00:00 to 11:59, while PM is defined from 12:00 to 23:59.
 * <p>
 * All date-time fields have an {@code int} value.
 * The {@code int} value follows {@link Calendar}, assigning 0 to AM and 1 to PM.
 * It is recommended that applications use the enum rather than the {@code int} value
 * to ensure code clarity.
 * <p>
 * <b>Do not use {@code ordinal()} to obtain the numeric representation of {@code AmPm}.
 * Use {@code getValue()} instead.</b>
 * <p>
 * This enum represents a common concept that is found in many calendar systems.
 * As such, this enum may be used by any calendar system that has the AM/PM
 * concept defined exactly equivalent to the ISO calendar system.
 * 
 * <h4>Implementation notes</h4>
 * This is an immutable and thread-safe enum.
 */
public enum AmPm implements AdjustableDateTime, DateTimeAdjuster {

    /**
     * The singleton instance for the morning, AM - ante meridiem.
     * This has the numeric value of {@code 0}.
     */
    AM,
    /**
     * The singleton instance for the afternoon, PM - post meridiem.
     * This has the numeric value of {@code 1}.
     */
    PM;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code AmPm} from an {@code int} value.
     * <p>
     * {@code AmPm} is an enum representing before and after midday.
     * This factory allows the enum to be obtained from the {@code int} value.
     * The {@code int} value follows {@link Calendar}, assigning 0 to AM and 1 to PM.
     *
     * @param amPmValue  the AM/PM value to represent, from 0 (AM) to 1 (PM)
     * @return the AM/PM, not null
     * @throws CalendricalException if the value is invalid
     */
    public static AmPm of(int amPmValue) {
        switch (amPmValue) {
            case 0: return AM;
            case 1: return PM;
            default: throw new CalendricalException("Invalid value for AM/PM: " + amPmValue);
        }
    }

    /**
     * Obtains an instance of {@code AmPm} from an hour-of-day.
     * <p>
     * {@code AmPm} is an enum representing before and after midday.
     * This factory allows the enum to be obtained from the hour-of-day value, from 0 to 23.
     *
     * @param hourOfDay  the hour-of-day to extract from, from 0 to 23
     * @return the AM/PM, not null
     * @throws CalendricalException if the hour-of-day is invalid
     */
    public static AmPm ofHour(int hourOfDay) {
        HOUR_OF_DAY.checkValidValue(hourOfDay);
        return hourOfDay < 12 ? AM : PM;
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code AmPm} from a calendrical.
     * <p>
     * A calendrical represents some form of date and time information.
     * This factory converts the arbitrary calendrical to an instance of {@code AmPm}.
     * 
     * @param calendrical  the calendrical to convert, not null
     * @return the AM/PM, not null
     * @throws CalendricalException if unable to convert to a {@code AmPm}
     */
    public static AmPm from(DateTime calendrical) {
        if (calendrical instanceof AmPm) {
            return (AmPm) calendrical;
        }
        return of((int) calendrical.get(AMPM_OF_DAY));
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the AM/PM {@code int} value.
     * <p>
     * The values are numbered following {@link Calendar}, assigning 0 to AM and 1 to PM.
     *
     * @return the AM/PM value, from 0 (AM) to 1 (PM)
     */
    public int getValue() {
        return ordinal();
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the textual representation, such as 'AM' or 'PM'.
     * <p>
     * This returns the textual name used to identify the am-pm.
     * The parameters control the length of the returned text and the locale.
     * <p>
     * If no textual mapping is found then the {@link #getValue() numeric value} is returned.
     *
     * @param locale  the locale to use, not null
     * @return the short text value of the AM/PM, not null
     */
    public String getText(TextStyle style, Locale locale) {
        throw new UnsupportedOperationException();  // TODO
//        return AMPM_OF_DAY.getText(getValue(), style, locale);
    }

    //-----------------------------------------------------------------------
    @Override
    public long get(DateTimeField field) {
        if (field == AMPM_OF_DAY) {
            return getValue();
        } else if (field instanceof LocalDateTimeField) {
            throw new CalendricalException(field.getName() + " not valid for AmPm");
        }
        return field.doGet(this);
    }

    @Override
    public AmPm with(DateTimeField field, long newValue) {
        if (field == AMPM_OF_DAY) {
            ((LocalDateTimeField) field).checkValidValue(newValue);
            return AmPm.of((int) newValue);
        } else if (field instanceof LocalDateTimeField) {
            throw new CalendricalException(field.getName() + " not valid for AmPm");
        }
        return field.doSet(this, newValue);
    }

    @Override
    public AmPm plus(long periodAmount, PeriodUnit unit) {
        if (unit instanceof LocalPeriodUnit) {
            switch ((LocalPeriodUnit) unit) {
                case HALF_DAYS: return (periodAmount % 2) == 0 ? this : (this == AM ? PM : AM);
                case DAYS:
                case WEEKS:
                    return this;
            }
            throw new CalendricalException(unit.getName() + " not valid for AmPm");
        }
        return unit.doAdd(this, periodAmount);
    }

    @Override
    public AmPm minus(long periodAmount, PeriodUnit unit) {
        return plus(DateTimes.safeNegate(periodAmount), unit);
    }

    //-----------------------------------------------------------------------
    /**
     * Extracts date-time information in a generic way.
     * <p>
     * This method exists to fulfill the {@link DateTime} interface.
     * This implementation returns the following types:
     * <ul>
     * <li>AmPm
     * <li>DateTimeBuilder, using {@link LocalDateTimeField#AMPM_OF_DAY}
     * <li>Class, returning {@code AmPm}
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
            return (R) new DateTimeBuilder(AMPM_OF_DAY, getValue());
        } else if (type == Class.class) {
            return (R) AmPm.class;
        } else if (type == AmPm.class) {
            return (R) this;
        }
        return null;
    }

    @Override
    public AdjustableDateTime doAdjustment(AdjustableDateTime calendrical) {
        return calendrical.with(AMPM_OF_DAY, getValue());
    }

}
