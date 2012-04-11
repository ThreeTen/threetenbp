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
package javax.time.chrono;

import java.io.Serializable;

import javax.time.CalendricalException;
import javax.time.DayOfWeek;
import javax.time.LocalDate;
import javax.time.MathUtils;
import javax.time.builder.CalendricalObject;

/**
 * A date in the Coptic calendar system.
 * <p>
 * The Coptic calendar system is primarily used in Egypt.
 * Dates are aligned such that {@code 0001AM-01-01 (Coptic)} is {@code 0284-08-29 (ISO)}.
 * <p>
 * This class is immutable and thread-safe.
 */
public final class CopticDate implements CalendricalObject, Serializable {

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = 1L;
    /**
     * The number of days to add to MJD to get the Coptic epoch day.
     */
    private static final int EPOCH_DAY_DIFFERENCE = 574971;  // TODO: correct value

    /**
     * The proleptic year.
     */
    private final int prolepticYear;
    /**
     * The month.
     */
    private final int month;
    /**
     * The day.
     */
    private final int day;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code CopticDate} from the era, year, month and day.
     *
     * @param era  the Coptic era, not null
     * @param year  the Coptic year, from 1 to the maximum supported
     * @param monthOfYear  the Coptic month, from 1 to 13
     * @param dayOfMonth  the Coptic day-of-month, from 1 to 30
     * @return the Coptic date, not null
     * @throws CalendricalException if the date is invalid
     */
    public static CopticDate of(CopticEra era, int year, int monthOfYear, int dayOfMonth) {
        return of(prolepticYear(era, year), monthOfYear, dayOfMonth);
    }

    /**
     * Obtains an instance of {@code CopticDate} from the proleptic-year, month and day.
     *
     * @param prolepticYear  the Coptic proleptic-year
     * @param monthOfYear  the Coptic month, from 1 to 13
     * @param dayOfMonth  the Coptic day-of-month, from 1 to 30
     * @return the Coptic date, not null
     * @throws CalendricalException if the date is invalid
     */
    public static CopticDate of(int prolepticYear, int monthOfYear, int dayOfMonth) {
        return new CopticDate(prolepticYear, monthOfYear, dayOfMonth);
    }

    /**
     * Obtains an instance of {@code CopticDate} from the proleptic-year, month and day.
     *
     * @param prolepticYear  the Coptic proleptic-year
     * @param month  the Coptic month, from 1 to 13
     * @param dayOfMonth  the Coptic day-of-month, from 1 to 30
     * @return the Coptic date, not null
     * @throws CalendricalException if the date is invalid
     */
    public static CopticDate from(CalendricalObject calendrical) {
        if (calendrical instanceof CopticDate) {
            return (CopticDate) calendrical;
        }
        LocalDate date = calendrical.extract(LocalDate.class);
        if (date == null) {
            throw new CalendricalException("Unable to create CopticDate from " + calendrical.getClass());
        }
        return ofEpochDay(date.toEpochDay());
    }

    /**
     * Obtains an instance of {@code CopticDate} from a number of epoch days.
     *
     * @param epochDay  the epoch day to convert based on 1970-01-01 (ISO)
     * @return the Coptic date, not null
     * @throws CalendricalException if the date is invalid
     */
    private static CopticDate ofEpochDay(long epochDay) {
//        if (epochDay < MIN_EPOCH_DAY || epochDay > MAX_EPOCH_DAY) {
//            throw new CalendricalRuleException("Date exceeds supported range for CopticDate", CopticChronology.YEAR);
//        }
        int prolepticYear = (int) (((epochDay * 4) + 1463) / 1461);
        int startYearEpochDay = (prolepticYear - 1) * 365 + (prolepticYear / 4);
        int doy0 = (int) (epochDay - startYearEpochDay);
        int month = doy0 / 30 + 1;
        int dom = doy0 % 30 + 1;
        return of(prolepticYear, month, dom);
    }

    private static int prolepticYear(CopticEra era, int yearOfEra) {
        return (era == CopticEra.AM ? yearOfEra : 1 - yearOfEra);
    }

    private static CopticDate resolvePreviousValid(int prolepticYear, int month, int day) {
        if (month == 13 && day > 5) {
            day = CopticChrono.INSTANCE.isLeapYear(prolepticYear) ? 6 : 5;
        }
        return of(prolepticYear, month, day);
    }

    //-----------------------------------------------------------------------
    /**
     * Creates an instance.
     * 
     * @param prolepticYear  the Coptic proleptic-year
     * @param monthOfYear  the Coptic month, from 1 to 13
     * @param dayOfMonth  the Coptic day-of-month, from 1 to 30
     * @throws CalendricalException if the date is invalid
     */
    private CopticDate(int prolepticYear, int monthOfYear, int dayOfMonth) {
        this.prolepticYear = prolepticYear;
        this.month = monthOfYear;
        this.day = dayOfMonth;
    }

    /**
     * Validates the object.
     *
     * @return the resolved date, not null
     */
    private Object readResolve() {
        // TODO: validate
        return this;
    }

    //-----------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    @Override
    public <T> T extract(Class<T> type) {
        if (type == LocalDate.class) {
            return (T) toLocalDate();
        }
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the value of the specified date field.
     *
     * @param field  the field to get, not null
     * @return the value for the field
     */
    public int get(ChronoField field) {
        switch (field) {
            case DAY_OF_WEEK: return getDayOfWeek().getValue();
            case DAY_OF_MONTH: return getDayOfMonth();
            case DAY_OF_YEAR: return getDayOfYear();
            case MONTH_OF_YEAR: return getMonthOfYear();
            case YEAR_OF_ERA: return getYearOfEra();
            case PROLEPTIC_YEAR: return prolepticYear;
            case ERA: return (prolepticYear >= 1 ? 1 : 0);
        }
        throw new CalendricalException("Unknown field");
    }

    /**
     * Gets the Coptic era.
     * <p>
     * There are two eras - the current era (AM) and the previous era (BAM).
     *
     * @return the Coptic era, not null
     */
    public CopticEra getEra() {
        return (prolepticYear >= 1 ? CopticEra.AM : CopticEra.BAM);
    }

    /**
     * Gets the Coptic proleptic-year.
     * <p>
     * The proleptic year is the same as the year-of-era for the current AM era.
     *
     * @return the Coptic proleptic-year
     */
    public int getProlepticYear() {
        return prolepticYear;
    }

    /**
     * Gets the Coptic year-of-era.
     * <p>
     * The year-of-era is the same as the proleptic-year for the current AM era.
     *
     * @return the Coptic year-of-era
     */
    public int getYearOfEra() {
        return (prolepticYear >= 1 ? prolepticYear : 1 - prolepticYear);
    }

    /**
     * Gets the Coptic month-of-year.
     * <p>
     * There are 13 months in a Coptic year, numbered 1 to 13.
     *
     * @return the Coptic month-of-year, from 1 to 13
     */
    public int getMonthOfYear() {
        return month;
    }

    /**
     * Gets the Coptic day-of-month field.
     * <p>
     * There are 30 days in each of the first 12 Coptic months, numbered 1 to 30.
     * The 13th month has 5 days, or 6 in a leap year, numbered 1 to 5 or 1 to 6.
     *
     * @return the Coptic day-of-month, from 1 to 30
     */
    public int getDayOfMonth() {
        return day;
    }

    /**
     * Gets the Coptic day-of-year field.
     * <p>
     * There are 365 days in a standard Coptic year and 366 in a leap year.
     * The days are numbered from 1 to 365 or 1 to 366.
     *
     * @return the Coptic day-of-year, from 1 to 365, or 366 in a leap year
     */
    public int getDayOfYear() {
        return (month - 1) * 30 + day;
    }

    /**
     * Gets the day-of-week field, which is an enum {@code DayOfWeek}.
     * <p>
     * The day-of-week in the Coptic calendar system is the same as that in the
     * standard ISO calendar system.
     *
     * @return the day-of-week, not null
     */
    public DayOfWeek getDayOfWeek() {
        return toLocalDate().getDayOfWeek();
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if the date is during a leap year.
     * <p>
     * This checks whether the year is a leap year according to the Coptic calendar system.
     *
     * @return true if the year is leap, false otherwise
     */
    public boolean isLeapYear() {
        return CopticChrono.INSTANCE.isLeapYear(prolepticYear);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this date with the specified field altered.
     * <p>
     * This method returns a new date based on this date with a new value for the specified field.
     * This can be used to change any field, for example to set the year, month of day-of-month.
     * <p>
     * In some cases, changing the specified field can cause the resulting date to become invalid.
     * If this occurs, then the day-of-month will be adjusted to the last valid day of the month.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param field  the field to set in the returned date, not null
     * @param newValue  the new value of the field in the returned date, not null
     * @return a {@code CopticDate} based on this date with the specified field set, not null
     */
    public CopticDate with(ChronoField field, int newValue) {
        MathUtils.checkNotNull(field, "ChronoField must not be null");
        // TODO: validate value
        int curValue = get(field);
        if (curValue == newValue) {
            return this;
        }
        switch (field) {
            case DAY_OF_WEEK: return plusDays(newValue - curValue);
            case DAY_OF_MONTH: return resolvePreviousValid(prolepticYear, month, newValue);
            case DAY_OF_YEAR: return resolvePreviousValid(prolepticYear, ((newValue - 1) / 30) + 1, ((newValue - 1) % 30) + 1);
            case MONTH_OF_YEAR: return resolvePreviousValid(prolepticYear, newValue, day);
            case YEAR_OF_ERA: return resolvePreviousValid(prolepticYear >= 1 ? newValue : 1 - newValue, month, day);
            case PROLEPTIC_YEAR: return resolvePreviousValid(newValue, month, day);
            case ERA: return resolvePreviousValid(1 - prolepticYear, month, day);
        }
        throw new CalendricalException("Unknown field");
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this date with the specified period in years added.
     * <p>
     * This adds the specified period in years to the date.
     * In some cases, adding years can cause the resulting date to become invalid.
     * If this occurs, then the day-of-month will be adjusted to the last valid day of the month.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param years  the years to add, may be negative
     * @return a {@code CopticDate} based on this date with the years added, not null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public CopticDate plusYears(long years) {
        return plusMonths(MathUtils.safeMultiply(years, 13));
    }

    /**
     * Returns a copy of this date with the specified period in months added.
     * <p>
     * This adds the specified period in months to the date.
     * In some cases, adding months can cause the resulting date to become invalid.
     * If this occurs, then the day-of-month will be adjusted to the last valid day of the month.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param months  the months to add, may be negative
     * @return a {@code CopticDate} based on this date with the months added, not null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public CopticDate plusMonths(long months) {
        if (months == 0) {
            return this;
        }
        long curEm = prolepticYear * 13L + (month - 1);
        long calcEm = MathUtils.safeAdd(curEm, months);
        int newYear = MathUtils.safeToInt(MathUtils.floorDiv(calcEm, 13));
        int newMonth = MathUtils.floorMod(calcEm, 13) + 1;
        return resolvePreviousValid(newYear, newMonth, day);
    }

    /**
     * Returns a copy of this date with the specified period in weeks added.
     * <p>
     * This adds the specified period in weeks to the date.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param weeks  the weeks to add, may be negative
     * @return a {@code CopticDate} based on this date with the weeks added, not null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public CopticDate plusWeeks(long weeks) {
        return plusDays(MathUtils.safeMultiply(weeks, 7));
    }

    /**
     * Returns a copy of this date with the specified number of days added.
     * <p>
     * This adds the specified period in days to the date.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param days  the days to add, may be negative
     * @return a {@code CopticDate} based on this date with the days added, not null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public CopticDate plusDays(long days) {
        if (days == 0) {
            return this;
        }
        return CopticDate.ofEpochDay(MathUtils.safeAdd(toEpochDay(), days));
    }

    //-----------------------------------------------------------------------
    /**
     * Converts this date to the standard epoch-day from 1970-01-01 (ISO).
     * <p>
     * This converts this Coptic date to the equivalent standard ISO date.
     * The conversion ensures that the date is accurate at midday.
     * 
     * @return the equivalent date, not null
     */
    public long toEpochDay() {
        long year = (long) prolepticYear;
        long copticEpochDay = (year * 365) + MathUtils.floorDiv(year, 4) + (getDayOfYear() - 1);
        return copticEpochDay - EPOCH_DAY_DIFFERENCE;
    }

    /**
     * Converts this date to the standard {@code LocalDate}.
     * <p>
     * This converts this Coptic date to the equivalent standard ISO date.
     * The conversion ensures that the date is accurate at midday.
     * 
     * @return the equivalent date, not null
     */
    public LocalDate toLocalDate() {
        return LocalDate.ofEpochDay(toEpochDay());
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
        int cmp = MathUtils.safeCompare(prolepticYear, other.prolepticYear);
        if (cmp == 0) {
            cmp = MathUtils.safeCompare(month, other.month);
            if (cmp == 0) {
                cmp = MathUtils.safeCompare(day, other.day);
            }
        }
        return cmp;
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if this date is equal to another date.
     * <p>
     * The comparison is based on the time-line position of the dates.
     *
     * @param obj  the object to check, null returns false
     * @return true if this is equal to the other date
     */
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof CopticDate) {
            CopticDate other = (CopticDate) object;
            return prolepticYear == other.prolepticYear && month == other.month && day == other.day;
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
        return Integer.rotateLeft(prolepticYear, 16) ^ (month << 8) ^ day;
    }

    //-----------------------------------------------------------------------
    /**
     * Outputs this date as a {@code String}, such as {@code 1723AM-13-01 (Coptic)}.
     * <p>
     * The output will be in the format {@code {year}{era}-{month}-{day} (Coptic)}.
     *
     * @return the formatted date, not null
     */
    @Override
    public String toString() {
        int yearValue = getYearOfEra();
        int monthValue = getMonthOfYear();
        int dayValue = getDayOfMonth();
        int absYear = Math.abs(yearValue);
        StringBuilder buf = new StringBuilder(12);
        if (absYear < 1000) {
            buf.append(yearValue + 10000).deleteCharAt(0);
        } else {
            buf.append(yearValue);
        }
        return buf.append(getEra()).append(monthValue < 10 ? "-0" : "-")
            .append(monthValue)
            .append(dayValue < 10 ? "-0" : "-")
            .append(dayValue)
            .append(" (Coptic)")
            .toString();
    }

}
