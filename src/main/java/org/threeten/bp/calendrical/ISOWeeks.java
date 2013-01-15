/*
 * Copyright (c) 2011-2012, Stephen Colebourne & Michael Nascimento Santos
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
package org.threeten.bp.calendrical;

import static org.threeten.bp.DayOfWeek.THURSDAY;
import static org.threeten.bp.DayOfWeek.WEDNESDAY;
import static org.threeten.bp.calendrical.ChronoField.DAY_OF_WEEK;
import static org.threeten.bp.calendrical.ChronoField.EPOCH_DAY;
import static org.threeten.bp.calendrical.ChronoField.YEAR;
import static org.threeten.bp.calendrical.ChronoUnit.FOREVER;
import static org.threeten.bp.calendrical.ChronoUnit.WEEKS;
import static org.threeten.bp.calendrical.ChronoUnit.YEARS;

import org.threeten.bp.Duration;
import org.threeten.bp.LocalDate;
import org.threeten.bp.calendrical.PeriodUnit.PeriodBetween;
import org.threeten.bp.jdk8.Jdk8Methods;

/**
 * Fields and units supporting the week-based-year defined by ISO-8601.
 * <p>
 * The ISO-8601 standard was originally intended as a data interchange format,
 * defining a string format for dates and times. However, it also defines an
 * alternate way of expressing the date, based on the concept of week-based-year.
 * <p>
 * The date is expressed using three fields:
 * <p><ul>
 * <li>{@link ChronoField#DAY_OF_WEEK DAY_OF_WEEK} - the standard field defining the
 *  day-of-week from Monday (1) to Sunday (7)
 * <li>{@link #WEEK_OF_WEEK_BASED_YEAR} - the week within the week-based-year
 * <li>{@link #WEEK_BASED_YEAR WEEK_BASED_YEAR} - the week-based-year
 * </ul><p>
 * The week-based-year itself is defined relative to the standard ISO proleptic year.
 * It differs from the standard year in that it always starts on a Monday.
 * <p>
 * The first week of a week-based-year is the first Monday-based week of the standard
 * ISO year that has at least 4 days in the new year.
 * <p><ul>
 * <li>If January 1st is Monday then week 1 starts on January 1st
 * <li>If January 1st is Tuesday then week 1 starts on December 31st of the previous standard year
 * <li>If January 1st is Wednesday then week 1 starts on December 30th of the previous standard year
 * <li>If January 1st is Thursday then week 1 starts on December 29th of the previous standard year
 * <li>If January 1st is Friday then week 1 starts on January 4th
 * <li>If January 1st is Saturday then week 1 starts on January 3rd
 * <li>If January 1st is Sunday then week 1 starts on January 2nd
 * </ul><p>
 * There are 52 weeks in most week-based years, however on occasion there are 53 weeks.
 * <p>
 * For example:
 * <p>
 * <table cellpadding="0" cellspacing="3" border="0" style="text-align: left; width: 50%;">
 * <tr><th>Date</th><th>Day-of-week</th><th>Field values</th></tr>
 * <tr><th>2008-12-28</th><td>Sunday</td><td>Week 52 of week-based-year 2008</td></tr>
 * <tr><th>2008-12-29</th><td>Monday</td><td>Week 1 of week-based-year 2009</td></tr>
 * <tr><th>2008-12-31</th><td>Wednesday</td><td>Week 1 of week-based-year 2009</td></tr>
 * <tr><th>2009-01-01</th><td>Thursday</td><td>Week 1 of week-based-year 2009</td></tr>
 * <tr><th>2009-01-04</th><td>Sunday</td><td>Week 1 of week-based-year 2009</td></tr>
 * <tr><th>2009-01-05</th><td>Monday</td><td>Week 2 of week-based-year 2009</td></tr>
 * </table>
 * <p>
 * This class is immutable and thread-safe.
 */
public final class ISOWeeks {

    /**
     * The field that represents the week-of-week-based-year.
     * <p>
     * This field allows the week of the week-based-year value to be queried and set.
     * <p>
     * This unit is an immutable and thread-safe enum.
     */
    public static final DateTimeField WEEK_OF_WEEK_BASED_YEAR = Field.WEEK_OF_WEEK_BASED_YEAR;
    /**
     * The field that represents the week-based-year.
     * <p>
     * This field allows the week-based-year value to be queried and set.
     * <p>
     * This unit is an immutable and thread-safe enum.
     */
    public static final DateTimeField WEEK_BASED_YEAR = Field.WEEK_BASED_YEAR;
    /**
     * The unit that represents week-based-years for the purpose of addition and subtraction.
     * <p>
     * This allows a number of week-based-years to be added to, or subtracted from, a date.
     * The unit is equal to either 52 or 53 weeks.
     * The estimated duration of a week-based-year is the same as that of a standard ISO
     * year at {@code 365.2425 Days}.
     * <p>
     * The rules for addition add the number of week-based-years to the existing value
     * for the week-based-year field. If the resulting week-based-year only has 52 weeks,
     * then the date will be in week 1 of the following week-based-year.
     * <p>
     * This unit is an immutable and thread-safe enum.
     */
    public static final PeriodUnit WEEK_BASED_YEARS = Unit.WEEK_BASED_YEARS;

    /**
     * Restricted constructor.
     */
    private ISOWeeks() {
        throw new AssertionError("Not instantiable");
    }

    //-----------------------------------------------------------------------
    /**
     * Implementation of the field.
     */
    private static enum Field implements DateTimeField {
        WEEK_OF_WEEK_BASED_YEAR {
            @Override
            public String getName() {
                return "WeekOfWeekBasedYear";
            }
            @Override
            public PeriodUnit getBaseUnit() {
                return WEEKS;
            }
            @Override
            public PeriodUnit getRangeUnit() {
                return WEEK_BASED_YEARS;
            }
            @Override
            public DateTimeValueRange range() {
                return DateTimeValueRange.of(1, 52, 53);
            }
            @Override
            public boolean doIsSupported(DateTimeAccessor dateTime) {
                return dateTime.isSupported(EPOCH_DAY);
            }
            @Override
            public DateTimeValueRange doRange(DateTimeAccessor dateTime) {
                return getWeekRange(LocalDate.from(dateTime));
            }
            @Override
            public long doGet(DateTimeAccessor dateTime) {
                return getWeek(LocalDate.from(dateTime));
            }
            @Override
            public <R extends DateTime> R doWith(R dateTime, long newValue) {
                DateTimeValueRange.of(1, 53).checkValidValue(newValue, this);
                return (R) dateTime.plus(Jdk8Methods.safeSubtract(newValue, doGet(dateTime)), WEEKS);
            }
        },
        WEEK_BASED_YEAR {
            @Override
            public String getName() {
                return "WeekBasedYear";
            }
            @Override
            public PeriodUnit getBaseUnit() {
                return WEEK_BASED_YEARS;
            }
            @Override
            public PeriodUnit getRangeUnit() {
                return FOREVER;
            }
            @Override
            public DateTimeValueRange range() {
                return YEAR.range();
            }
            @Override
            public boolean doIsSupported(DateTimeAccessor dateTime) {
                return dateTime.isSupported(EPOCH_DAY);
            }
            @Override
            public DateTimeValueRange doRange(DateTimeAccessor dateTime) {
                return YEAR.range();
            }
            @Override
            public long doGet(DateTimeAccessor dateTime) {
                return getWeekBasedYear(LocalDate.from(dateTime));
            }
            @Override
            public <R extends DateTime> R doWith(R dateTime, long newValue) {
                int newVal = range().checkValidIntValue(newValue, WEEK_BASED_YEAR);
                LocalDate date = LocalDate.from(dateTime);
                int week = getWeek(date);
                date = date.withDayOfYear(180).withYear(newVal).with(WEEK_OF_WEEK_BASED_YEAR, week);
                return (R) date.with(date);
            }
        };

        @Override
        public boolean resolve(DateTimeBuilder builder, long value) {
            Long[] values = builder.queryFieldValues(WEEK_BASED_YEAR, WEEK_OF_WEEK_BASED_YEAR, DAY_OF_WEEK);
            if (values[0] != null && values[1] != null && values[2] != null) {
                int wby = WEEK_BASED_YEAR.range().checkValidIntValue(values[0], WEEK_BASED_YEAR);
                int week = WEEK_OF_WEEK_BASED_YEAR.range().checkValidIntValue(values[1], WEEK_OF_WEEK_BASED_YEAR);
                int dow = DAY_OF_WEEK.range().checkValidIntValue(values[2], DAY_OF_WEEK);
                LocalDate date = LocalDate.of(wby, 2, 1).with(WEEK_OF_WEEK_BASED_YEAR, week).with(DAY_OF_WEEK, dow);
                builder.addFieldValue(EPOCH_DAY, date.toEpochDay());
                builder.removeFieldValues(WEEK_BASED_YEAR, WEEK_OF_WEEK_BASED_YEAR, DAY_OF_WEEK);
            }
            return false;
        }

        // JDK8 default interface
        //-------------------------------------------------------------------------
        @Override
        public int compare(DateTimeAccessor dateTime1, DateTimeAccessor dateTime2) {
            return Long.compare(dateTime1.getLong(this), dateTime2.getLong(this));
        }

        private static DateTimeValueRange getWeekRange(LocalDate date) {
            int wby = getWeekBasedYear(date);
            date = date.withDayOfYear(1).withYear(wby);
            // 53 weeks if standard year starts on Thursday, or Wed in a leap year
            if (date.getDayOfWeek() == THURSDAY || (date.getDayOfWeek() == WEDNESDAY && date.isLeapYear())) {
                return DateTimeValueRange.of(1, 53);
            }
            return DateTimeValueRange.of(1, 52);
        }

        private static int getWeek(LocalDate date) {
            int dow0 = date.getDayOfWeek().ordinal();
            int doy0 = date.getDayOfYear() - 1;
            int doyThu0 = doy0 + (3 - dow0);  // adjust to mid-week Thursday (which is 3 indexed from zero)
            int alignedWeek = doyThu0 / 7;
            int firstThuDoy0 = doyThu0 - (alignedWeek * 7);
            int firstMonDoy0 = firstThuDoy0 - 3;
            if (firstMonDoy0 < -3) {
                firstMonDoy0 += 7;
            }
            if (doy0 < firstMonDoy0) {
                return (int) getWeekRange(date.withDayOfYear(180).minusYears(1)).getMaximum();
            }
            int week = ((doy0 - firstMonDoy0) / 7) + 1;
            if (week == 53) {
                if ((firstMonDoy0 == -3 || (firstMonDoy0 == -2 && date.isLeapYear())) == false) {
                    week = 1;
                }
            }
            return week;
        }

        private static int getWeekBasedYear(LocalDate date) {
            int year = date.getYear();
            int doy = date.getDayOfYear();
            if (doy <= 3) {
                int dow = date.getDayOfWeek().ordinal();
                if (doy - dow < -2) {
                    year--;
                }
            } else if (doy >= 363) {
                int dow = date.getDayOfWeek().ordinal();
                doy = doy - 363 - (date.isLeapYear() ? 1 : 0);
                if (doy - dow >= 0) {
                    year++;
                }
            }
            return year;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Implementation of the period unit.
     */
    private static enum Unit implements PeriodUnit {
        WEEK_BASED_YEARS;

        @Override
        public String getName() {
            return "WeekBasedYears";
        }

        @Override
        public Duration getDuration() {
            return YEARS.getDuration();
        }

        @Override
        public boolean isDurationEstimated() {
            return true;
        }

        @Override
        public boolean isSupported(DateTime dateTime) {
            return dateTime.isSupported(EPOCH_DAY);
        }

        @Override
        public <R extends DateTime> R doPlus(R dateTime, long periodToAdd) {
            return (R) dateTime.with(WEEK_BASED_YEAR, Jdk8Methods.safeAdd(dateTime.get(WEEK_BASED_YEAR), periodToAdd));
        }

        @Override
        public <R extends DateTime> PeriodBetween between(R dateTime1, R dateTime2) {
            long period = Jdk8Methods.safeSubtract(dateTime2.getLong(WEEK_BASED_YEAR), dateTime1.getLong(WEEK_BASED_YEAR));
            return new Between(period, WEEK_BASED_YEARS);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Implementation of {@code PeriodBetween}.
     */
    private static final class Between implements PeriodBetween {
        private final long amount;
        private final PeriodUnit unit;

        Between(long amount, PeriodUnit unit) {
            this.amount = amount;
            this.unit = unit;
        }

        @Override
        public long getAmount() {
            return amount;
        }

        @Override
        public PeriodUnit getUnit() {
            return unit;
        }

        @Override
        public DateTime doPlusAdjustment(DateTime dateTime) {
            return dateTime.plus(amount, unit);
        }

        @Override
        public DateTime doMinusAdjustment(DateTime dateTime) {
            return dateTime.minus(amount, unit);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Between) {
                Between other = (Between) obj;
                return amount == other.amount && unit.equals(other.unit);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return ((int) (amount ^ (amount >>> 32))) ^ unit.hashCode();
        };

        @Override
        public String toString() {
            return amount + " " + unit;
        }
    }

}
