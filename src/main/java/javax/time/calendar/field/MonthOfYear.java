/*
 * Copyright (c) 2007, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time.calendar.field;

import javax.time.calendar.Calendrical;
import javax.time.calendar.CalendricalState;
import javax.time.calendar.ISOChronology;
import javax.time.calendar.TimeFieldRule;

/**
 * A time field representing a month of year.
 * <p>
 * MonthOfYear is an immutable time field that can only store a month of year.
 * It is a type-safe way of representing a month of year in an application.
 * <p>
 * <b>Do not use ordinal() to obtain the numeric representation of a MonthOfYear
 * instance. Use getMonthOfYear() instead.</b>
 * <p>
 * MonthOfYear is thread-safe and immutable.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
public enum MonthOfYear implements Calendrical {

    /**
     * The singleton instance for the month of January.
     */
    JANUARY(1),
    /**
     * The singleton instance for the month of February.
     */
    FEBRUARY(2) {
        /** {@inheritDoc} */
        @Override
        public int lengthInDays(int year) {
            if (ISOChronology.INSTANCE.isLeapYear(year)) {
                return 29;
            }
            return 28;
        }
        /** {@inheritDoc} */
        @Override
        public int minLengthInDays() {
            return 28;
        }
        /** {@inheritDoc} */
        @Override
        public int maxLengthInDays() {
            return 29;
        }
    },
    /**
     * The singleton instance for the month of March.
     */
    MARCH(3),
    /**
     * The singleton instance for the month of April.
     */
    APRIL(4),
    /**
     * The singleton instance for the month of May.
     */
    MAY(5),
    /**
     * The singleton instance for the month of June.
     */
    JUNE(6),
    /**
     * The singleton instance for the month of July.
     */
    JULY(7),
    /**
     * The singleton instance for the month of August.
     */
    AUGUST(8),
    /**
     * The singleton instance for the month of September.
     */
    SEPTEMBER(9),
    /**
     * The singleton instance for the month of October.
     */
    OCTOBER(10),
    /**
     * The singleton instance for the month of November.
     */
    NOVEMBER(11),
    /**
     * The singleton instance for the month of December.
     */
    DECEMBER(12),
    ;
    /**
     * The rule implementation that defines how the month of year field operates.
     */
    public static final TimeFieldRule RULE = new Rule();

    /**
     * The month of year being represented.
     */
    private final int monthOfYear;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>MonthOfYear</code>.
     *
     * @param monthOfYear  the month of year to represent
     * @return the existing MonthOfYear
     */
    public static MonthOfYear monthOfYear(int monthOfYear) {
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
                throw new IllegalArgumentException("MonthOfYear cannot have the value " + monthOfYear);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance with the specified month of year.
     *
     * @param monthOfYear  the month of year to represent
     */
    private MonthOfYear(int monthOfYear) {
        this.monthOfYear = monthOfYear;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the month of year value.
     *
     * @return the month of year
     */
    public int getMonthOfYear() {
        return monthOfYear;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the calendrical state which provides internal access to this
     * MonthOfYear instance.
     *
     * @return the calendar state for this instance, never null
     */
    @Override
    public CalendricalState getCalendricalState() {
        return null;  // TODO
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the next month of year wrapping so that the next month of year
     * is always returned.
     *
     * @return the next month of year, never null
     */
    public MonthOfYear next() {
        return values()[(ordinal() + 1) % 12];
    }

    /**
     * Gets the previous month of year wrapping so that the previous month of year
     * is always returned.
     *
     * @return the previous month of year, never null
     */
    public MonthOfYear previous() {
        return values()[(ordinal() + 12 - 1) % 12];
    }

    //-----------------------------------------------------------------------
    /**
     * Is this month of year instance greater than the specified month of year.
     *
     * @param otherMonthOfYear  the other month of year instance, not null
     * @return true if this month of year is greater
     * @throws NullPointerException if otherMonthOfYear is null
     */
    public boolean isGreaterThan(MonthOfYear otherMonthOfYear) {
        return compareTo(otherMonthOfYear) > 0;
    }

    /**
     * Is this month of year instance less than the specified month of year.
     *
     * @param otherMonthOfYear  the other month of year instance, not null
     * @return true if this month of year is less
     * @throws NullPointerException if otherMonthOfYear is null
     */
    public boolean isLessThan(MonthOfYear otherMonthOfYear) {
        return compareTo(otherMonthOfYear) < 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the length of this month in days.
     *
     * @param year  the year to obtain the length for
     * @return the length of this month in days, from 28 to 31
     */
    public int lengthInDays(int year) {
        return maxLengthInDays();  // overridden by FEBRUARY
    }

    /**
     * Gets the minimum length of this month in days.
     *
     * @return the minimum length of this month in days, from 28 to 31
     */
    public int minLengthInDays() {
        switch (this) {
            case FEBRUARY:
                return 28;  // overridden by FEBRUARY
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
     *
     * @return the maximum length of this month in days, from 29 to 31
     */
    public int maxLengthInDays() {
        switch (this) {
            case FEBRUARY:
                return 29;  // overridden by FEBRUARY
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
     * Gets the quarter that this month falls in.
     *
     * @return the quarter of year, never null
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
     *
     * @return the month of season, from 1 to 3
     */
    public int getMonthOfQuarter() {
        return (ordinal() % 3) + 1;
    }

    //-----------------------------------------------------------------------
    /**
     * Implementation of the rules for the month of year field.
     */
    private static class Rule extends TimeFieldRule {

        /** Constructor. */
        protected Rule() {
            super("MonthOfYear", null, null, 1, 12);
        }
    }

}
