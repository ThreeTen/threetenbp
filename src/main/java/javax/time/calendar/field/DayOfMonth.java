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

import java.io.Serializable;

import javax.time.calendar.Calendrical;
import javax.time.calendar.CalendricalState;
import javax.time.calendar.TimeFieldRule;

/**
 * A time field representing a day of month.
 * <p>
 * DayOfMonth is an immutable time field that can only store a day of month.
 * It is a type-safe way of representing a day of month in an application.
 * <p>
 * Static factory methods allow you to construct instances.
 * The day of month may be queried using getDayOfMonth().
 * <p>
 * DayOfMonth is thread-safe and immutable.
 *
 * @author Stephen Colebourne
 */
public final class DayOfMonth implements Calendrical, Comparable<DayOfMonth>, Serializable {

    /**
     * The rule implementation that defines how the day of month field operates.
     */
    public static final TimeFieldRule RULE = new Rule();
    /**
     * A serialization identifier for this instance.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The day of month being represented.
     */
    private final int dayOfMonth;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>DayOfMonth</code>.
     *
     * @param dayOfMonth  the day of month to represent
     * @return the created DayOfMonth
     */
    public static DayOfMonth dayOfMonth(int dayOfMonth) {
        return new DayOfMonth(dayOfMonth);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance with the specified day of month.
     *
     * @param dayOfMonth  the day of month to represent
     */
    private DayOfMonth(int dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the day of month value.
     *
     * @return the day of month
     */
    public int getDayOfMonth() {
        return dayOfMonth;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the calendrical state which provides internal access to this
     * DayOfMonth instance.
     *
     * @return the calendar state for this instance, never null
     */
    @Override
    public CalendricalState getCalendricalState() {
        return null;  // TODO
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this day of month instance to another.
     *
     * @param otherDayOfMonth  the other day of month instance, not null
     * @return the comparator value, negative if less, postive if greater
     * @throws NullPointerException if otherDayOfMonth is null
     */
    public int compareTo(DayOfMonth otherDayOfMonth) {
        int thisValue = this.dayOfMonth;
        int otherValue = otherDayOfMonth.dayOfMonth;
        return (thisValue < otherValue ? -1 : (thisValue == otherValue ? 0 : 1));
    }

    /**
     * Is this day of month instance greater than the specified day of month.
     *
     * @param otherDayOfMonth  the other day of month instance, not null
     * @return true if this day of month is greater
     * @throws NullPointerException if otherDayOfMonth is null
     */
    public boolean isGreaterThan(DayOfMonth otherDayOfMonth) {
        return compareTo(otherDayOfMonth) > 0;
    }

    /**
     * Is this day of month instance less than the specified day of month.
     *
     * @param otherDayOfMonth  the other day of month instance, not null
     * @return true if this day of month is less
     * @throws NullPointerException if otherDayOfMonth is null
     */
    public boolean isLessThan(DayOfMonth otherDayOfMonth) {
        return compareTo(otherDayOfMonth) < 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Is this instance equal to that specified, evaluating the day of month.
     *
     * @param otherDayOfMonth  the other day of month instance, null returns false
     * @return true if the day of month is the same
     */
    @Override
    public boolean equals(Object otherDayOfMonth) {
        if (this == otherDayOfMonth) {
            return true;
        }
        if (otherDayOfMonth instanceof DayOfMonth) {
            return dayOfMonth == ((DayOfMonth) otherDayOfMonth).dayOfMonth;
        }
        return false;
    }

    /**
     * A hashcode for the day of month object.
     *
     * @return a suitable hashcode
     */
    @Override
    public int hashCode() {
        return dayOfMonth;
    }

//  /**
//  * A map holding the maximum number of days per month.
//  */
// private static final Map<MonthOfYear, Days> STANDARD_DAYS_IN_MONTH = new EnumMap<MonthOfYear, Days>(MonthOfYear.class);
// /**
//  * A map holding the maximum number of days per month.
//  */
// private static final Map<MonthOfYear, Days> LEAP_YEAR_DAYS_IN_MONTH = new EnumMap<MonthOfYear, Days>(MonthOfYear.class);
// static {
//     STANDARD_DAYS_IN_MONTH.put(MonthOfYear.JANUARY, days(31));
//     STANDARD_DAYS_IN_MONTH.put(MonthOfYear.FEBRUARY, days(28));
//     STANDARD_DAYS_IN_MONTH.put(MonthOfYear.MARCH, days(31));
//     STANDARD_DAYS_IN_MONTH.put(MonthOfYear.APRIL, days(30));
//     STANDARD_DAYS_IN_MONTH.put(MonthOfYear.MAY, days(31));
//     STANDARD_DAYS_IN_MONTH.put(MonthOfYear.JUNE, days(30));
//     STANDARD_DAYS_IN_MONTH.put(MonthOfYear.JULY, days(31));
//     STANDARD_DAYS_IN_MONTH.put(MonthOfYear.AUGUST, days(31));
//     STANDARD_DAYS_IN_MONTH.put(MonthOfYear.SEPTEMBER, days(30));
//     STANDARD_DAYS_IN_MONTH.put(MonthOfYear.OCTOBER, days(31));
//     STANDARD_DAYS_IN_MONTH.put(MonthOfYear.NOVEMBER, days(30));
//     STANDARD_DAYS_IN_MONTH.put(MonthOfYear.DECEMBER, days(31));
//     LEAP_YEAR_DAYS_IN_MONTH.putAll(STANDARD_DAYS_IN_MONTH);
//     LEAP_YEAR_DAYS_IN_MONTH.put(MonthOfYear.FEBRUARY, days(29));
// }

    //-----------------------------------------------------------------------
    /**
     * Implementation of the rules for the day of month field.
     */
    private static class Rule extends TimeFieldRule {

        /** Constructor. */
        protected Rule() {
            super("DayOfMonth", null, null, 1, 31);
        }

        /** {@inheritDoc} */
        @Override
        public int getValue(CalendricalState calState) {
            return super.getValue(calState) + 1;
        }
        /** {@inheritDoc} */
        @Override
        public boolean isFixedValueSet() {
            return false;
        }
        /** {@inheritDoc} */
        @Override
        public int getSmallestMaximumValue() {
            return 28;
        }
        /** {@inheritDoc} */
        @Override
        public int getMaximumValue(Calendrical calendricalContext) {
            if (calendricalContext != null) {
                if (calendricalContext.getCalendricalState().isSupported(MonthOfYear.RULE)) {
                    int month = calendricalContext.getCalendricalState().get(MonthOfYear.RULE);
                    switch (month) {
                        case 2:
                            if (calendricalContext.getCalendricalState().isSupported(Year.RULE)) {
                                int year = calendricalContext.getCalendricalState().get(Year.RULE);
                                if (Year.isLeap(year)) {
                                    return 29;
                                }
                            }
                            return 28;
                        case 4:
                        case 6:
                        case 9:
                        case 11:
                            return 30;
                        default:
                            return 31;
                    }
                }
            }
            return 31;
        }
        /** {@inheritDoc} */
        @Override
        public int getSmallestMaximumValue(Calendrical calendricalContext) {
            if (calendricalContext == null) {
                return 28;
            }
            if (calendricalContext.getCalendricalState().isSupported(MonthOfYear.RULE)) {
                int month = calendricalContext.getCalendricalState().get(MonthOfYear.RULE);
                switch (month) {
                    case 2:
                        if (calendricalContext.getCalendricalState().isSupported(Year.RULE)) {
                            int year = calendricalContext.getCalendricalState().get(Year.RULE);
                            if (Year.isLeap(year)) {
                                return 29;
                            }
                        }
                        return 28;
                    case 4:
                    case 6:
                    case 9:
                    case 11:
                        return 30;
                    default:
                        return 31;
                }
            }
            if (calendricalContext.getCalendricalState().isSupported(Year.RULE)) {
                int year = calendricalContext.getCalendricalState().get(Year.RULE);
                if (Year.isLeap(year)) {
                    return 29;
                }
            }
            return 28;
        }
    }

}
