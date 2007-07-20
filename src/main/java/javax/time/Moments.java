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
package javax.time;

import java.util.TimeZone;

/**
 * A set of utility methods for working with moments in the Java Date Framework.
 *
 * @author Stephen Colebourne
 */
public final class Moments {

    /**
     * Provides access to the current time.
     *
     * @return the current time, never null
     */
    public static Now now() {
        return Now.system();
    }

    //-----------------------------------------------------------------------
    /**
     * Gets an instance of Year.
     */
    public static Year year(int year) {
        return Year.year(year);
    }

    /**
     * Gets an instance of MonthOfYear.
     */
    public static MonthOfYear monthOfYear(int monthOfYear) {
        return MonthOfYear.monthOfYear(monthOfYear);
    }

    /**
     * Gets an instance of MonthOfYear.
     */
    public static MonthOfYear january() {
        return MonthOfYear.JANUARY;
    }

    /**
     * Gets an instance of MonthOfYear.
     */
    public static MonthOfYear february() {
        return MonthOfYear.FEBRUARY;
    }

    /**
     * Gets an instance of MonthOfYear.
     */
    public static MonthOfYear march() {
        return MonthOfYear.MARCH;
    }

    /**
     * Gets an instance of DayOfMonth.
     */
    public static DayOfMonth dayOfMonth(int dayOfMonth) {
        return DayOfMonth.dayOfMonth(dayOfMonth);
    }

    /**
     * Gets an instance of HourOfDay.
     */
    public static HourOfDay hourOfDay(int hourOfDay) {
        return HourOfDay.hourOfDay(hourOfDay);
    }

    /**
     * Gets an instance of MinuteOfHour.
     */
    public static MinuteOfHour minuteOfHour(int minuteOfHour) {
        return MinuteOfHour.minuteOfHour(minuteOfHour);
    }

    /**
     * Gets an instance of SecondOfMinute.
     */
    public static SecondOfMinute secondOfMinute(int secondOfMinute) {
        return SecondOfMinute.secondOfMinute(secondOfMinute);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets an instance of Years.
     */
    public static Years years(int years) {
        return Years.years(years);
    }

    /**
     * Gets an instance of Months.
     */
    public static Months months(int months) {
        return Months.months(months);
    }

    /**
     * Gets an instance of Weeks.
     */
    public static Weeks weeks(int weeks) {
        return Weeks.weeks(weeks);
    }

    /**
     * Gets an instance of Days.
     */
    public static Days days(int days) {
        return Days.days(days);
    }

    /**
     * Gets an instance of Hours.
     */
    public static Hours hours(int hours) {
        return Hours.hours(hours);
    }

    /**
     * Gets an instance of Minutes.
     */
    public static Minutes minutes(int minutes) {
        return Minutes.minutes(minutes);
    }

    /**
     * Gets an instance of Seconds.
     */
    public static Seconds seconds(int seconds) {
        return Seconds.seconds(seconds);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets an instance of CalendarDay.
     */
    public static CalendarDay calendarDay(int year, int monthOfYear, int dayOfMonth) {
        return CalendarDay.yearMonthDay(year, monthOfYear, dayOfMonth);
    }

    /**
     * Gets an instance of CalendarDay.
     */
    public static CalendarDay calendarDay(Year year, MonthOfYear monthOfYear, DayOfMonth dayOfMonth) {
        return CalendarDay.calendarDay(year, monthOfYear, dayOfMonth);
    }

    /**
     * Gets an instance of CalendarDay.
     */
    public static CalendarDay today() {
        return CalendarDay.yearMonthDay(2007, 6, 1);
    }

    /**
     * Gets an instance of CalendarDay.
     */
    public static CalendarDay yesterday() {
        return CalendarDay.yearMonthDay(2007, 6, 1);
    }

    /**
     * Gets an instance of CalendarDay.
     */
    public static CalendarDay tomorrow() {
        return CalendarDay.yearMonthDay(2007, 6, 1);
    }

    /**
     * Gets an instance of CalendarMonth.
     */
    public static CalendarMonth currentMonth() {
        return CalendarMonth.yearMonth(2007, 6);
    }

    /**
     * Gets an instance of TimeOfDay.
     */
    public static TimeOfDay currentTime() {
        return TimeOfDay.timeOfDay(12, 30);
    }

    /**
     * Gets an instance of TimeOfDay.
     */
    public static MomentBuilder moment() {
        return new MomentBuilder();
    }

    public static class MomentBuilder {
        public MomentWithYearBuilder year(int year) {
            return new MomentWithYearBuilder();
        }
        public MomentWithYearBuilder currentYear() {
            return new MomentWithYearBuilder();
        }
        public MomentWithYearBuilder nextYear() {
            return new MomentWithYearBuilder();
        }
        public MomentWithYearBuilder previousYear() {
            return new MomentWithYearBuilder();
        }
        public MomentBuilder zone(TimeZone zone) {
            return new MomentBuilder();
        }
        public MomentBuilder zoneID(String str) {
            return new MomentBuilder();
        }
        public MomentBuilder defaultZone() {
            return new MomentBuilder();
        }
    }
    public static class MomentWithYearBuilder {
        public MomentWithYearMonthBuilder monthOfYear(int monthOfYear) {
            return new MomentWithYearMonthBuilder();
        }
        public MomentWithYearMonthBuilder january() {
            return new MomentWithYearMonthBuilder();
        }
        public MomentWithYearMonthBuilder february() {
            return new MomentWithYearMonthBuilder();
        }
        public MomentWithYearMonthBuilder march() {
            return new MomentWithYearMonthBuilder();
        }
        public MomentWithYearMonthBuilder april() {
            return new MomentWithYearMonthBuilder();
        }
        public MomentWithYearMonthBuilder may() {
            return new MomentWithYearMonthBuilder();
        }
        public MomentWithYearMonthBuilder june() {
            return new MomentWithYearMonthBuilder();
        }
        public MomentWithYearMonthBuilder july() {
            return new MomentWithYearMonthBuilder();
        }
        public MomentWithYearMonthBuilder august() {
            return new MomentWithYearMonthBuilder();
        }
        public MomentWithYearMonthBuilder september() {
            return new MomentWithYearMonthBuilder();
        }
        public MomentWithYearMonthBuilder october() {
            return new MomentWithYearMonthBuilder();
        }
        public MomentWithYearMonthBuilder november() {
            return new MomentWithYearMonthBuilder();
        }
        public MomentWithYearMonthBuilder december() {
            return new MomentWithYearMonthBuilder();
        }
        public MomentWithYearMonthDayBuilder dayOfYear(int dayOfYear) {
            return new MomentWithYearMonthDayBuilder();
        }
    }
    public static class MomentWithYearMonthBuilder {
        public MomentWithYearMonthDayBuilder dayOfMonth(int dayOfMonth) {
            return new MomentWithYearMonthDayBuilder();
        }
        public CalendarMonth resolve() {
            return CalendarMonth.yearMonth(2007, 6);
        }
        public CalendarMonth resolveLenient() {
            return CalendarMonth.yearMonth(2007, 6);
        }
    }
    public static class MomentWithYearMonthDayBuilder {
        public CalendarDay resolve() {
            return CalendarDay.yearMonthDay(2007, 6, 1);
        }
        public CalendarDay resolveLenient() {
            return CalendarDay.yearMonthDay(2007, 6, 1);
        }
    }
}
