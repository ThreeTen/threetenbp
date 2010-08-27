/*
 * Copyright (c) 2007-2010 Stephen Colebourne & Michael Nascimento Santos
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

import java.util.Locale;

import javax.time.calendar.format.DateTimeFormatterBuilder.TextStyle;

/**
 * A month-of-year, such as 'July'.
 * <p>
 * {@code MonthOfYear} is an enum representing the 12 months of the year -
 * January, February, March, April, May, June, July, August, September, October,
 * November and December.
 * <p>
 * In addition to the textual enum name, each month-of-year has an {@code int} value.
 * The {@code int} value follows normal usage and the ISO-8601 standard,
 * from 1 (January) to 12 (December). It is recommended that applications use the enum
 * rather than the {@code int} value to ensure code clarity.
 * <p>
 * <b>Do not use {@code ordinal()} to obtain the numeric representation of {@code MonthOfYear}.
 * Use {@code getValue()} instead.</b>
 * <p>
 * This enum represents a common concept that is found in many calendar systems.
 * As such, this enum may be used by any calendar system that has the month-of-year
 * concept with a twelve month year where the names are equivalent to those defined.
 * Note that the implementation of {@link DateTimeFieldRule} for month-of-year may
 * vary by calendar system.
 * <p>
 * MonthOfYear is an immutable and thread-safe enum.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
public enum MonthOfYear implements Calendrical {

    /**
     * The singleton instance for the month of January.
     * This has the numeric value of {@code 1}.
     */
    JANUARY,
    /**
     * The singleton instance for the month of February.
     * This has the numeric value of {@code 2}.
     */
    FEBRUARY,
    /**
     * The singleton instance for the month of March.
     * This has the numeric value of {@code 3}.
     */
    MARCH,
    /**
     * The singleton instance for the month of April.
     * This has the numeric value of {@code 4}.
     */
    APRIL,
    /**
     * The singleton instance for the month of May.
     * This has the numeric value of {@code 5}.
     */
    MAY,
    /**
     * The singleton instance for the month of June.
     * This has the numeric value of {@code 6}.
     */
    JUNE,
    /**
     * The singleton instance for the month of July.
     * This has the numeric value of {@code 7}.
     */
    JULY,
    /**
     * The singleton instance for the month of August.
     * This has the numeric value of {@code 8}.
     */
    AUGUST,
    /**
     * The singleton instance for the month of September.
     * This has the numeric value of {@code 9}.
     */
    SEPTEMBER,
    /**
     * The singleton instance for the month of October.
     * This has the numeric value of {@code 10}.
     */
    OCTOBER,
    /**
     * The singleton instance for the month of November.
     * This has the numeric value of {@code 11}.
     */
    NOVEMBER,
    /**
     * The singleton instance for the month of December.
     * This has the numeric value of {@code 12}.
     */
    DECEMBER;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code MonthOfYear} from an {@code int} value.
     * <p>
     * {@code MonthOfYear} is an enum representing the 12 months of the year.
     * This factory allows the enum to be obtained from the {@code int} value.
     * The {@code int} value follows the ISO-8601 standard, from 1 (January) to 12 (December).
     * <p>
     * An exception is thrown if the value is invalid. The exception uses the
     * {@link ISOChronology} month-of-year rule to indicate the failed rule.
     *
     * @param monthOfYear  the month-of-year to represent, from 1 (January) to 12 (December)
     * @return the MonthOfYear singleton, never null
     * @throws IllegalCalendarFieldValueException if the month-of-year is invalid
     */
    public static MonthOfYear of(int monthOfYear) {
        switch (monthOfYear) {
            case 1:
                return JANUARY;
            case 2:
                return FEBRUARY;
            case 3:
                return MARCH;
            case 4:
                return APRIL;
            case 5:
                return MAY;
            case 6:
                return JUNE;
            case 7:
                return JULY;
            case 8:
                return AUGUST;
            case 9:
                return SEPTEMBER;
            case 10:
                return OCTOBER;
            case 11:
                return NOVEMBER;
            case 12:
                return DECEMBER;
            default:
                throw new IllegalCalendarFieldValueException(ISOChronology.monthOfYearRule(), monthOfYear, 1, 12);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the month-of-year {@code int} value.
     * <p>
     * The values are numbered following the ISO-8601 standard,
     * from 1 (January) to 12 (December).
     *
     * @return the month-of-year, from 1 (January) to 12 (December)
     */
    public int getValue() {
        return ordinal() + 1;
    }

    /**
     * Gets the value of the specified calendrical rule.
     * <p>
     * This returns the one of the month values if the type of the rule
     * is {@code MonthOfYear}. Other rules will return {@code null}.
     *
     * @param rule  the rule to use, not null
     * @return the value for the rule, null if the value cannot be returned
     */
    public <T> T get(CalendricalRule<T> rule) {
        if (rule.getReifiedType() != MonthOfYear.class) {
            return null;
        }
        return rule.reify(this);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the short textual representation of this month-of-year, such as 'Jan' or 'Dec'.
     * <p>
     * This method is notionally specific to {@link ISOChronology} as it uses
     * the month-of-year rule to obtain the text. However, it is expected that
     * the text will be equivalent for all month-of-year rules, thus this aspect
     * of the implementation should be irrelevant to applications.
     * <p>
     * If there is no textual mapping for the locale, then the value is
     * returned as per {@link Integer#toString()}.
     *
     * @param locale  the locale to use, not null
     * @return the short text value of the month-of-year, never null
     */
    public String getShortText(Locale locale) {
        return ISOChronology.monthOfYearRule().getText(getValue(), locale, TextStyle.SHORT);
    }

    /**
     * Gets the full textual representation of this month-of-year, such as 'January' or 'December'.
     * <p>
     * This method is notionally specific to {@link ISOChronology} as it uses
     * the month-of-year rule to obtain the text. However, it is expected that
     * the text will be equivalent for all month-of-year rules, thus this aspect
     * of the implementation should be irrelevant to applications.
     * <p>
     * If there is no textual mapping for the locale, then the value is
     * returned as per {@link Integer#toString()}.
     *
     * @param locale  the locale to use, not null
     * @return the full text value of the month-of-year, never null
     */
    public String getText(Locale locale) {
        return ISOChronology.monthOfYearRule().getText(getValue(), locale, TextStyle.FULL);
    }

    //-----------------------------------------------------------------------
    /**
     * Is this instance representing January.
     *
     * @return true if this instance represents January
     */
    public boolean isJanuary() {
        return (this == JANUARY);
    }

    /**
     * Is this instance representing February.
     *
     * @return true if this instance represents February
     */
    public boolean isFebruary() {
        return (this == FEBRUARY);
    }

    /**
     * Is this instance representing March.
     *
     * @return true if this instance represents March
     */
    public boolean isMarch() {
        return (this == MARCH);
    }

    /**
     * Is this instance representing April.
     *
     * @return true if this instance represents April
     */
    public boolean isApril() {
        return (this == APRIL);
    }

    /**
     * Is this instance representing May.
     *
     * @return true if this instance represents May
     */
    public boolean isMay() {
        return (this == MAY);
    }

    /**
     * Is this instance representing June.
     *
     * @return true if this instance represents June
     */
    public boolean isJune() {
        return (this == JUNE);
    }

    /**
     * Is this instance representing July.
     *
     * @return true if this instance represents July
     */
    public boolean isJuly() {
        return (this == JULY);
    }

    /**
     * Is this instance representing August.
     *
     * @return true if this instance represents August
     */
    public boolean isAugust() {
        return (this == AUGUST);
    }

    /**
     * Is this instance representing September.
     *
     * @return true if this instance represents September
     */
    public boolean isSeptember() {
        return (this == SEPTEMBER);
    }

    /**
     * Is this instance representing October.
     *
     * @return true if this instance represents October
     */
    public boolean isOctober() {
        return (this == OCTOBER);
    }

    /**
     * Is this instance representing November.
     *
     * @return true if this instance represents November
     */
    public boolean isNovember() {
        return (this == NOVEMBER);
    }

    /**
     * Is this instance representing December.
     *
     * @return true if this instance represents December
     */
    public boolean isDecember() {
        return (this == DECEMBER);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the next month-of-year.
     * <p>
     * This calculates based on the time-line, thus it rolls around the end of
     * the year. The next month after December is January.
     *
     * @return the next month-of-year, never null
     */
    public MonthOfYear next() {
        return roll(1);
    }

    /**
     * Gets the previous month-of-year.
     * <p>
     * This calculates based on the time-line, thus it rolls around the end of
     * the year. The previous month before January is December.
     *
     * @return the previous month-of-year, never null
     */
    public MonthOfYear previous() {
        return roll(-1);
    }

    /**
     * Rolls the month-of-year, adding the specified number of months.
     * <p>
     * This calculates based on the time-line, thus it rolls around the end of
     * the year from December to January. The months to roll by may be negative.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param months  the months to roll by, positive or negative
     * @return the resulting month-of-year, never null
     */
    public MonthOfYear roll(int months) {
        return values()[(ordinal() + (months % 12 + 12)) % 12];
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the length of this month in days.
     * <p>
     * This takes a flag to determine whether to return the length for a leap year or not.
     * <p>
     * February has 28 days in a standard year and 29 days in a leap year.
     * April, June, September and November have 30 days.
     * All other months have 31 days.
     *
     * @param leapYear  true if the length is required for a leap year
     * @return the length of this month in days, from 28 to 31
     */
    public int lengthInDays(boolean leapYear) {
        switch (this) {
            case FEBRUARY:
                return (leapYear ? 29 : 28);
            case APRIL:
            case JUNE:
            case SEPTEMBER:
            case NOVEMBER:
                return 30;
            default:
                return 31;
        }
    }

    /**
     * Gets the minimum length of this month in days.
     * <p>
     * February has a minimum length of 28 days.
     * April, June, September and November have 30 days.
     * All other months have 31 days.
     *
     * @return the minimum length of this month in days, from 28 to 31
     */
    public int minLengthInDays() {
        switch (this) {
            case FEBRUARY:
                return 28;
            case APRIL:
            case JUNE:
            case SEPTEMBER:
            case NOVEMBER:
                return 30;
            default:
                return 31;
        }
    }

    /**
     * Gets the maximum length of this month in days.
     * <p>
     * February has a maximum length of 29 days.
     * April, June, September and November have 30 days.
     * All other months have 31 days.
     *
     * @return the maximum length of this month in days, from 29 to 31
     */
    public int maxLengthInDays() {
        switch (this) {
            case FEBRUARY:
                return 29;
            case APRIL:
            case JUNE:
            case SEPTEMBER:
            case NOVEMBER:
                return 30;
            default:
                return 31;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the day-of-month for last day of this month.
     * <p>
     * This is a synonym for {@link #lengthInDays(boolean)} and exists to provide
     * a more meaningful API.
     *
     * @param leapYear  true if the length is required for a leap year
     * @return the last day of this month, from 28 to 31
     */
    public int getLastDayOfMonth(boolean leapYear) {
        return lengthInDays(leapYear);
    }

//    /**
//     * Gets the day-of-year for the first day of this month.
//     * <p>
//     * This returns the day-of-year that this month begins on, using the leap
//     * year flag to determine the length of February.
//     *
//     * @param leapYear  true if the length is required for a leap year
//     * @return the last day of this month, from 1 to 335
//     */
//    public int getMonthStartDayOfYear(boolean leapYear) {
//        return 0;  // TODO
//    }
//
//    /**
//     * Gets the day-of-year for the first day of this month.
//     * <p>
//     * This returns the day-of-year that this month ends on, using the leap
//     * year flag to determine the length of February.
//     *
//     * @param leapYear  true if the length is required for a leap year
//     * @return the last day of this month, from 31 to 366
//     */
//    public int getMonthEndDayOfYear(boolean leapYear) {
//        return 0;  // TODO
//    }

    //-----------------------------------------------------------------------
    /**
     * Gets the quarter that this month falls in.
     * <p>
     * January to March are Q1, April to June are Q2, July to September are Q3
     * and October to December are Q4.
     *
     * @return the quarter-of-year, never null
     */
    public QuarterOfYear getQuarterOfYear() {
        if (ordinal() < 3) {
            return QuarterOfYear.Q1;
        } else if (ordinal() < 6) {
            return QuarterOfYear.Q2;
        } else if (ordinal() < 9) {
            return QuarterOfYear.Q3;
        } else {
            return QuarterOfYear.Q4;
        }
    }

    /**
     * Gets the index of the month within the quarter.
     * <p>
     * January, April, July and October will return 1.
     * February, May, August and November will return 2.
     * March, June, September and December will return 3.
     *
     * @return the month of season, from 1 to 3
     */
    public int getMonthOfQuarter() {
        return (ordinal() % 3) + 1;
    }

}
