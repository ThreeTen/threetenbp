/*
 * Copyright (c) 2007-2011, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time.i18n;

import java.io.Serializable;

import javax.time.CalendricalException;
import javax.time.MathUtils;
import javax.time.calendar.Calendrical;
import javax.time.calendar.CalendricalEngine;
import javax.time.calendar.CalendricalRule;
import javax.time.calendar.CalendricalRuleException;
import javax.time.calendar.DayOfWeek;
import javax.time.calendar.IllegalCalendarFieldValueException;
import javax.time.calendar.InvalidCalendarFieldException;
import javax.time.calendar.LocalDate;

/**
 * A date in the Coptic calendar system.
 * <p>
 * {@code CopticDate} is an immutable class that represents a date in the Coptic calendar system.
 * The rules of the calendar system are described in {@link CopticChronology}.
 * <p>
 * Instances of this class may be created from other date objects that implement {@code Calendrical}.
 * Notably this includes {@link LocalDate} and all other date classes from other calendar systems.
 * <p>
 * CopticDate is immutable and thread-safe.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
public final class CopticDate
        implements Calendrical, Comparable<CopticDate>, Serializable {

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The minimum valid year.
     */
    public static final int MIN_YEAR = 1;
    /**
     * The maximum valid year.
     * This is currently set to 9999 but may be changed to increase the valid range
     * in a future version of the specification.
     */
    public static final int MAX_YEAR = 9999;
    /**
     * The number of days to add to MJD to get the Coptic epoch day.
     */
    private static final int MJD_TO_COPTIC = 574971;
    /**
     * The minimum epoch day that is valid.
     * The avoidance of negatives makes calculation easier.
     */
    private static final int MIN_EPOCH_DAY = 0;
    /**
     * The maximum epoch day that is valid.
     * The low maximum year means that overflows don't happen.
     */
    private static final int MAX_EPOCH_DAY = 3652134;  // (9999 - 1) * 365 + (9999 / 4) + 30 * (13 - 1) + 6 - 1

    /**
     * The Coptic epoch day count, 0001-01-01 = 0.
     */
    private final int epochDay;
    /**
     * The Coptic year.
     */
    private final transient int year;
    /**
     * The Coptic month.
     */
    private final transient int month;
    /**
     * The Coptic day.
     */
    private final transient int day;

    //-----------------------------------------------------------------------
    /**
     * Gets the rule for {@code CopticDate}.
     *
     * @return the rule for the date, not null
     */
    public static CalendricalRule<CopticDate> rule() {
        return Rule.INSTANCE;
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code CopticDate} from the Coptic year,
     * month-of-year and day-of-month.
     *
     * @param copticYear  the year to represent, from 1 to 9999
     * @param copticMonthOfYear  the month-of-year to represent, from 1 to 13
     * @param copticDayOfMonth  the day-of-month to represent, from 1 to 30
     * @return the Coptic date, not null
     * @throws IllegalCalendarFieldValueException if the value of any field is out of range
     * @throws InvalidCalendarFieldException if the day-of-month is invalid for the month-year
     */
    public static CopticDate of(int copticYear, int copticMonthOfYear, int copticDayOfMonth) {
        CopticChronology.YEAR.checkValidValue(copticYear);
        CopticChronology.MONTH_OF_YEAR.checkValidValue(copticMonthOfYear);
        CopticChronology.DAY_OF_MONTH.checkValidValue(copticDayOfMonth);
        if (copticMonthOfYear == 13 && copticDayOfMonth > 5) {
            if (copticDayOfMonth > 6 || CopticChronology.isLeapYear(copticYear) == false) {
                throw new InvalidCalendarFieldException("Invalid Coptic date", CopticChronology.DAY_OF_MONTH);
            }
        }
        int epochDay = (copticYear - 1) * 365 + (copticYear / 4) + 30 * (copticMonthOfYear - 1) + copticDayOfMonth - 1;
        return new CopticDate(epochDay, copticYear, copticMonthOfYear, copticDayOfMonth);
    }

    /**
     * Obtains an instance of {@code CopticDate} using the previous valid algorithm.
     *
     * @param copticYear  the year to represent, from 1 to 9999
     * @param copticMonthOfYear  the month-of-year to represent, from 1 to 13
     * @param copticDayOfMonth  the day-of-month to represent, from 1 to 30
     * @return the Coptic date, not null
     */
    private static CopticDate copticDatePreviousValid(int copticYear, int copticMonthOfYear, int copticDayOfMonth) {
        CopticChronology.YEAR.checkValidValue(copticYear);
        CopticChronology.MONTH_OF_YEAR.checkValidValue(copticMonthOfYear);
        CopticChronology.DAY_OF_MONTH.checkValidValue(copticDayOfMonth);
        if (copticMonthOfYear == 13 && copticDayOfMonth > 5) {
            copticDayOfMonth = CopticChronology.isLeapYear(copticYear) ? 6 : 5;
        }
        int epochDay = (copticYear - 1) * 365 + (copticYear / 4) + 30 * (copticMonthOfYear - 1) + copticDayOfMonth - 1;
        return new CopticDate(epochDay, copticYear, copticMonthOfYear, copticDayOfMonth);
    }

    /**
     * Obtains an instance of {@code CopticDate} from a calendrical.
     * <p>
     * This can be used extract the date directly from any implementation
     * of {@code Calendrical}, including those in other calendar systems.
     *
     * @param calendrical  the calendrical to extract from, not null
     * @return the Coptic date, not null
     * @throws CalendricalException if the date cannot be obtained
     */
    public static CopticDate of(Calendrical calendrical) {
        return rule().getValueChecked(calendrical);
    }

    /**
     * Obtains an instance of {@code CopticDate} from a number of epoch days.
     *
     * @param epochDay  the epoch days to use, not null
     * @return a CopticDate object, not null
     * @throws IllegalCalendarFieldValueException if the year range is exceeded
     */
    private static CopticDate copticDateFromEpochDay(int epochDay) {
        if (epochDay < MIN_EPOCH_DAY || epochDay > MAX_EPOCH_DAY) {
            throw new CalendricalRuleException("Date exceeds supported range for CopticDate", CopticChronology.YEAR);
        }
        int year = ((epochDay * 4) + 1463) / 1461;
        int startYearEpochDay = (year - 1) * 365 + (year / 4);
        int doy0 = epochDay - startYearEpochDay;
        int month = doy0 / 30 + 1;
        int day = doy0 % 30 + 1;
        return new CopticDate(epochDay, year, month, day);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance with the specified date.
     *
     * @param epochDay  the Coptic epoch days, caller checked to be one or greater
     * @param year  the year to represent, caller calculated
     * @param month  the month-of-year to represent, caller calculated
     * @param day  the day-of-month to represent, caller calculated
     */
    private CopticDate(int epochDay, int year, int month, int day) {
        this.epochDay = epochDay;
        this.year = year;
        this.month = month;
        this.day = day;
    }

    /**
     * Replaces the date instance from the stream with a valid one.
     *
     * @return the resolved date, not null
     */
    private Object readResolve() {
        return copticDateFromEpochDay(epochDay);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the value of the specified calendrical rule.
     * <p>
     * This method queries the value of the specified calendrical rule.
     * If the value cannot be returned for the rule from this date then
     * {@code null} will be returned.
     *
     * @param ruleToDerive  the rule to derive, not null
     * @return the value for the rule, null if the value cannot be returned
     */
    public <T> T get(CalendricalRule<T> ruleToDerive) {
        if (ruleToDerive == rule()) {
            return ruleToDerive.reify(this);
        }
        return CalendricalEngine.derive(ruleToDerive, rule(), toLocalDate(), null, null, null, CopticChronology.INSTANCE, null);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the Coptic year field.
     *
     * @return the year, from MIN_YEAR to MAX_YEAR
     */
    public int getYear() {
        return year;
    }

    /**
     * Gets the Coptic month-of-year field.
     *
     * @return the month-of-year, from 1 to 13
     */
    public int getMonthOfYear() {
        return month;
    }

    /**
     * Gets the Coptic day-of-month field.
     *
     * @return the day-of-month, from 1 to 30
     */
    public int getDayOfMonth() {
        return day;
    }

    /**
     * Gets the Coptic day-of-year field.
     *
     * @return the day-of-year, from 1 to 365, or 366 in a leap year
     */
    public int getDayOfYear() {
        int startYearEpochDay = (year - 1) * 365 + (year / 4);
        return epochDay - startYearEpochDay + 1;
    }

    /**
     * Gets the day-of-week field, which is an enum {@code DayOfWeek}.
     * <p>
     * This method returns the enum {@link DayOfWeek} for the day-of-week.
     * This avoids confusion as to what {@code int} values mean.
     * If you need access to the primitive {@code int} value then the enum
     * provides the {@link DayOfWeek#getValue() int value}.
     * <p>
     * Additional information can be obtained from the {@code DayOfWeek}.
     * This includes textual names of the values.
     *
     * @return the day-of-week, not null
     */
    public DayOfWeek getDayOfWeek() {
        return DayOfWeek.of((epochDay + 4) % 7 + 1);
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if the year is a leap year, according to the Coptic calendar system rules.
     *
     * @return true if this date is in a leap year
     */
    public boolean isLeapYear() {
        return CopticChronology.isLeapYear(getYear());
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this date with the year altered.
     * <p>
     * This method changes the year of the date.
     * If this date is the leap day (month 13, day 6) and the new year is not
     * a leap year, the resulting date will be invalid.
     * To avoid this, the result day-of-month is changed from 6 to 5.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param year  the year to set in the returned date, from 1 to 9999
     * @return a {@code CopticDate} based on this date with the requested year, not null
     * @throws IllegalCalendarFieldValueException if the year value is invalid
     */
    public CopticDate withYear(int year) {
        return copticDatePreviousValid(year, getMonthOfYear(), getDayOfMonth());
    }

    /**
     * Returns a copy of this date with the month-of-year altered.
     * <p>
     * This method changes the month-of-year of the date.
     * If this month is from 1 to 12 and the new month is 13 then the
     * resulting date might be invalid. In this case, the last valid
     * day-of-the month will be returned.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param monthOfYear  the month-of-year to set in the returned date, from 1 to 13
     * @return a {@code CopticDate} based on this date with the requested month, not null
     * @throws IllegalCalendarFieldValueException if the month value is invalid
     */
    public CopticDate withMonthOfYear(int monthOfYear) {
        return copticDatePreviousValid(getYear(), monthOfYear, getDayOfMonth());
    }

    /**
     * Returns a copy of this date with the day-of-month altered.
     * <p>
     * This method changes the day-of-month of the date.
     * If the resulting date is invalid, an exception is thrown.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param dayOfMonth  the day-of-month to set in the returned date, from 1 to 30
     * @return a {@code CopticDate} based on this date with the requested day, not null
     * @throws IllegalCalendarFieldValueException if the day-of-month value is invalid
     * @throws InvalidCalendarFieldException if the day-of-month is invalid for the month-year
     */
    public CopticDate withDayOfMonth(int dayOfMonth) {
        return of(getYear(), getMonthOfYear(), dayOfMonth);
    }

    /**
     * Returns a copy of this date with the day-of-year altered.
     * <p>
     * This method changes the day-of-year of the date.
     * If the resulting date is invalid, an exception is thrown.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param dayOfYear  the day-of-year to set in the returned date, from 1 to 365-366
     * @return a {@code CopticDate} based on this date with the requested day, not null
     * @throws IllegalCalendarFieldValueException if the day-of-year value is invalid
     * @throws InvalidCalendarFieldException if the day-of-year is invalid for the year
     */
    public CopticDate withDayOfYear(int dayOfYear) {
        dayOfYear--;
        return of(getYear(), dayOfYear / 30 + 1, dayOfYear % 30 + 1);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this date with the specified number of years added.
     * <p>
     * This method adds the specified amount in years to the date.
     * If the month-day is invalid for the year, then the previous valid day
     * will be selected instead.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param years  the years to add, may be negative
     * @return a {@code CopticDate} based on this date with the years added, not null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public CopticDate plusYears(int years) {
        int newYear = getYear() + years;  // may overflow, but caught in factory
        return copticDatePreviousValid(newYear, getMonthOfYear(), getDayOfMonth());
    }

    /**
     * Returns a copy of this date with the specified number of months added.
     * <p>
     * This method adds the specified amount in months to the date.
     * If the month-day is invalid for the year, then the previous valid day
     * will be selected instead.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param months  the months to add, may be negative
     * @return a {@code CopticDate} based on this date with the months added, not null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public CopticDate plusMonths(int months) {
        int newMonth0 = getMonthOfYear() + months - 1;  // may overflow, but caught in factory
        int years = newMonth0 / 13;
        newMonth0 = newMonth0 % 13;
        if (newMonth0 < 0) {
            newMonth0 += 13;
            years--;
        }
        int newYear = getYear() + years;  // may overflow, but caught in factory
        int newDay = getDayOfMonth();
        return copticDatePreviousValid(newYear, newMonth0 + 1, newDay);
    }

    /**
     * Returns a copy of this date with the specified number of weeks added.
     * <p>
     * This method adds the specified amount in weeks to the date.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param weeks  the weeks to add, may be negative
     * @return a {@code CopticDate} based on this date with the weeks added, not null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public CopticDate plusWeeks(int weeks) {
        int newEpochDay = epochDay + MathUtils.safeMultiply(weeks, 7);  // may overflow, but caught in factory
        return copticDateFromEpochDay(newEpochDay);
    }

    /**
     * Returns a copy of this date with the specified number of days added.
     * <p>
     * This method adds the specified amount in days to the date.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param days  the days to add, may be negative
     * @return a {@code CopticDate} based on this date with the days added, not null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public CopticDate plusDays(int days) {
        int newEpochDay = epochDay + days;  // may overflow, but caught in factory
        return copticDateFromEpochDay(newEpochDay);
    }

    //-----------------------------------------------------------------------
    /**
     * Converts this date to a {@code LocalDate}, which is the default representation
     * of a date, and provides values in the ISO-8601 calendar system.
     *
     * @return the equivalent date in the ISO-8601 calendar system, not null
     */
    public LocalDate toLocalDate() {
        return LocalDate.ofModifiedJulianDay(epochDay - MJD_TO_COPTIC);
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this date to another date.
     * <p>
     * The comparison is based on the time-line position of the dates.
     *
     * @param other  the other date to compare to, not null
     * @return the comparator value, negative if less, positive if greater
     */
    public int compareTo(CopticDate other) {
        return MathUtils.safeCompare(epochDay, other.epochDay);
    }

    /**
     * Checks if this date is after the specified date.
     * <p>
     * The comparison is based on the time-line position of the dates.
     *
     * @param other  the other date to compare to, not null
     * @return true if this is after the specified date
     */
    public boolean isAfter(CopticDate other) {
        return epochDay > other.epochDay;
    }

    /**
     * Checks if this date is before the specified date.
     * <p>
     * The comparison is based on the time-line position of the dates.
     *
     * @param other  the other date to compare to, not null
     * @return true if this is before the specified date
     */
    public boolean isBefore(CopticDate other) {
        return epochDay < other.epochDay;
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if this date is equal to the specified date.
     * <p>
     * The comparison is based on the time-line position of the dates.
     *
     * @param other  the other date to compare to, null returns false
     * @return true if this is equal to the specified date
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof CopticDate) {
            CopticDate otherDate = (CopticDate) other;
            return epochDay == otherDate.epochDay;
        }
        return false;
    }

    /**
     * A hash code for this date.
     *
     * @return a suitable hash code
     */
    @Override
    public int hashCode() {
        return epochDay;
    }

    //-----------------------------------------------------------------------
    /**
     * Outputs this date as a {@code String}, such as {@code 1723-13-01 (Coptic)}.
     * <p>
     * The output will be in the format {@code yyyy-MM-dd (Coptic)}.
     *
     * @return the formatted date, not null
     */
    @Override
    public String toString() {
        int yearValue = getYear();
        int monthValue = getMonthOfYear();
        int dayValue = getDayOfMonth();
        int absYear = Math.abs(yearValue);
        StringBuilder buf = new StringBuilder(12);
        if (absYear < 1000) {
            buf.append(yearValue + 10000).deleteCharAt(0);
        } else {
            buf.append(yearValue);
        }
        return buf.append(monthValue < 10 ? "-0" : "-")
            .append(monthValue)
            .append(dayValue < 10 ? "-0" : "-")
            .append(dayValue)
            .append(" (Coptic)")
            .toString();
    }

    //-----------------------------------------------------------------------
    /**
     * Rule implementation.
     */
    static final class Rule extends CalendricalRule<CopticDate> implements Serializable {
        private static final CalendricalRule<CopticDate> INSTANCE = new Rule();
        private static final long serialVersionUID = 1L;
        private Rule() {
            super(CopticDate.class, "CopticDate");
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        protected CopticDate deriveFrom(CalendricalEngine engine) {
            LocalDate date = engine.getDate(true);
            if (date == null) {
                return null;
            }
            long epochDay = date.toModifiedJulianDay() + MJD_TO_COPTIC;
            return copticDateFromEpochDay((int) epochDay);
        }
    }

}
