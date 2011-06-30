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
package javax.time.calendar;

import static javax.time.calendar.ISODateTimeRule.DAY_OF_YEAR;

import java.io.Serializable;

import javax.time.MathUtils;

/**
 * The ISO-8601 calendar system, which follows the rules of the current
 * <i>de facto</i> world calendar.
 * <p>
 * ISOChronology follows the rules of the Gregorian calendar for all time.
 * Thus, dates is the past, and particularly before 1583, may not correspond
 * to historical documents.
 * <p>
 * ISOChronology is immutable and thread-safe.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
public final class ISOChronology extends Chronology implements Serializable {

    /**
     * The singleton instance of {@code ISOChronology}.
     */
    public static final ISOChronology INSTANCE = new ISOChronology();
    /**
     * A serialization identifier for this class.
     */
    private static final long serialVersionUID = 1L;
    /**
     * The number of seconds in one day.
     */
    static final int SECONDS_PER_DAY = 60 * 60 * 24;
    /**
     * The number of days in a 400 year cycle.
     */
    static final int DAYS_PER_CYCLE = 146097;
    /**
     * The number of days from year zero to year 1970.
     * There are five 400 year cycles from year zero to 2000.
     * There are 7 leap years from 1970 to 2000.
     */
    static final long DAYS_0000_TO_1970 = (DAYS_PER_CYCLE * 5L) - (30L * 365L + 7L);
    /**
     * The number of days from year zero to the Modified Julian Day epoch of 1858-11-17.
     */
    static final long DAYS_0000_TO_MJD_EPOCH = 678941;

    //-----------------------------------------------------------------------
    /**
     * Checks if the specified year is a leap year according to the ISO calendar system rules.
     * <p>
     * The ISO calendar system applies the current rules for leap years across the whole time-line.
     * In general, a year is a leap year if it is divisible by four without
     * remainder. However, years divisible by 100, are not leap years, with
     * the exception of years divisible by 400 which are.
     * <p>
     * For example, 1904 is a leap year it is divisible by 4.
     * 1900 was not a leap year as it is divisible by 100, however 2000 was a
     * leap year as it is divisible by 400.
     * <p>
     * The calculation is proleptic - applying the same rules into the far future and far past.
     * This is historically inaccurate, but is correct for the ISO-8601 standard.
     *
     * @param year  the year to check, may be outside the valid range for the rule
     * @return true if the year is a leap year
     */
    public static boolean isLeapYear(long year) {
        return ((year & 3) == 0) && ((year % 100) != 0 || (year % 400) == 0);
    }

    //-----------------------------------------------------------------------
    /**
     * Validates that the input value is not null.
     *
     * @param object  the object to check
     * @param errorMessage  the error to throw
     * @throws NullPointerException if the object is null
     */
    static void checkNotNull(Object object, String errorMessage) {
        if (object == null) {
            throw new NullPointerException(errorMessage);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Calculates the day-of-week from a date.
     *
     * @param date  the date to use, not null
     * @return the day-of-week
     */
    static DayOfWeek getDayOfWeekFromDate(LocalDate date) {
        long mjd = date.toModifiedJulianDay();
        if (mjd < 0) {
            long weeks = mjd / 7;
            mjd += (-weeks + 1) * 7;
        }
        int dow0 = (int) ((mjd + 2) % 7);
        return DayOfWeek.of(dow0 + 1);
    }

    //-----------------------------------------------------------------------
    /**
     * Calculates the day-of-year from a date.
     *
     * @param date  the date to use, not null
     * @return the day-of-year
     */
    static int getDayOfYearFromDate(LocalDate date) {
        return date.getMonthOfYear().getMonthStartDayOfYear(date.isLeapYear()) + date.getDayOfMonth() - 1;
    }

    /**
     * Calculates the date from a year and day-of-year.
     *
     * @param year  the year, valid
     * @param dayOfYear  the day-of-year, valid
     * @return the date, not null
     */
    static LocalDate getDateFromDayOfYear(int year, int dayOfYear) {
        DAY_OF_YEAR.checkValidValue(dayOfYear);
        boolean leap = ISOChronology.isLeapYear(year);
        if (dayOfYear == 366 && leap == false) {
            throw new InvalidCalendarFieldException("DayOfYear 366 is invalid for year " + year, DAY_OF_YEAR);
        }
        MonthOfYear moy = MonthOfYear.of((dayOfYear - 1) / 31 + 1);
        int monthEnd = moy.getMonthEndDayOfYear(leap);
        if (dayOfYear > monthEnd) {
            moy = moy.next();
        }
        int dom = dayOfYear - moy.getMonthStartDayOfYear(leap) + 1;
        return LocalDate.of(year, moy, dom);
    }

    //-----------------------------------------------------------------------
    /**
     * Calculates the week-based-year.
     *
     * @param date  the date, not null
     * @return the week-based-year
     */
    static int getWeekBasedYearFromDate(LocalDate date) {
        int year = date.getYear();  // use ISO year object so previous/next are checked
        if (date.getMonthOfYear() == MonthOfYear.JANUARY) {
            int dom = date.getDayOfMonth();
            if (dom < 4) {
                int dow = date.getDayOfWeek().getValue();
                if (dow > dom + 3) {
                    year--;
                }
            }
        } else if (date.getMonthOfYear() == MonthOfYear.DECEMBER) {
            int dom = date.getDayOfMonth();
            if (dom > 28) {
                int dow = date.getDayOfWeek().getValue();
                if (dow <= dom % 7) {
                    year++;
                }
            }
        }
        return year;
    }

    /**
     * Calculates the week of week-based-year.
     *
     * @param date  the date to use, not null
     * @return the week
     */
    static int getWeekOfWeekBasedYearFromDate(LocalDate date) {
        int wby = getWeekBasedYearFromDate(date);
        LocalDate yearStart = LocalDate.of(wby, MonthOfYear.JANUARY, 4);
        return MathUtils.safeToInt((date.toModifiedJulianDay() - yearStart.toModifiedJulianDay() +
                yearStart.getDayOfWeek().getValue() - 1) / 7 + 1);
    }

    //-----------------------------------------------------------------------
    /**
     * Restrictive constructor.
     */
    private ISOChronology() {
    }

    /**
     * Resolves singleton.
     *
     * @return the singleton instance
     */
    private Object readResolve() {
        return INSTANCE;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the name of the chronology.
     *
     * @return the name of the chronology, not null
     */
    @Override
    public String getName() {
        return "ISO";
    }

}
