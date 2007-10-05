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

import javax.time.calendar.CalendarDay;
import javax.time.calendar.CalendarMonth;
import javax.time.calendar.CalendarYear;
import javax.time.calendar.TimeOfDay;
import javax.time.calendar.field.DayOfMonth;
import javax.time.calendar.field.HourOfDay;
import javax.time.calendar.field.MinuteOfHour;
import javax.time.calendar.field.MonthOfYear;
import javax.time.calendar.field.SecondOfMinute;

/**
 * A set of utility methods for working with dates, times and durations in
 * the Java Time Framework.
 *
 * @author Stephen Colebourne
 */
public final class Times {

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
    public static CalendarYear year(int year) {
        return CalendarYear.year(year);
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
     * Gets an instance of CalendarDay.
     */
    public static CalendarDay calendarDay(int year, int monthOfYear, int dayOfMonth) {
        return CalendarDay.yearMonthDay(year, monthOfYear, dayOfMonth);
    }

    /**
     * Gets an instance of CalendarDay.
     */
    public static CalendarDay calendarDay(CalendarYear year, MonthOfYear monthOfYear, DayOfMonth dayOfMonth) {
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
     * Gets a builder.
     */
    public static CalendarBuilder calendar() {
        return new CalendarBuilder();
    }

    public static class CalendarBuilder {
        public CalendarWithYearBuilder year(int year) {
            return new CalendarWithYearBuilder();
        }
        public CalendarWithYearBuilder currentYear() {
            return new CalendarWithYearBuilder();
        }
        public CalendarWithYearBuilder nextYear() {
            return new CalendarWithYearBuilder();
        }
        public CalendarWithYearBuilder previousYear() {
            return new CalendarWithYearBuilder();
        }
        public CalendarBuilder zone(TimeZone zone) {
            return new CalendarBuilder();
        }
        public CalendarBuilder zoneID(String str) {
            return new CalendarBuilder();
        }
        public CalendarBuilder defaultZone() {
            return new CalendarBuilder();
        }
    }
    public static class CalendarWithYearBuilder {
        public CalendarWithYearMonthBuilder monthOfYear(int monthOfYear) {
            return new CalendarWithYearMonthBuilder();
        }
        public CalendarWithYearMonthBuilder january() {
            return new CalendarWithYearMonthBuilder();
        }
        public CalendarWithYearMonthBuilder february() {
            return new CalendarWithYearMonthBuilder();
        }
        public CalendarWithYearMonthBuilder march() {
            return new CalendarWithYearMonthBuilder();
        }
        public CalendarWithYearMonthBuilder april() {
            return new CalendarWithYearMonthBuilder();
        }
        public CalendarWithYearMonthBuilder may() {
            return new CalendarWithYearMonthBuilder();
        }
        public CalendarWithYearMonthBuilder june() {
            return new CalendarWithYearMonthBuilder();
        }
        public CalendarWithYearMonthBuilder july() {
            return new CalendarWithYearMonthBuilder();
        }
        public CalendarWithYearMonthBuilder august() {
            return new CalendarWithYearMonthBuilder();
        }
        public CalendarWithYearMonthBuilder september() {
            return new CalendarWithYearMonthBuilder();
        }
        public CalendarWithYearMonthBuilder october() {
            return new CalendarWithYearMonthBuilder();
        }
        public CalendarWithYearMonthBuilder november() {
            return new CalendarWithYearMonthBuilder();
        }
        public CalendarWithYearMonthBuilder december() {
            return new CalendarWithYearMonthBuilder();
        }
        public CalendarWithYearMonthDayBuilder dayOfYear(int dayOfYear) {
            return new CalendarWithYearMonthDayBuilder();
        }
    }
    public static class CalendarWithYearMonthBuilder {
        public CalendarWithYearMonthDayBuilder dayOfMonth(int dayOfMonth) {
            return new CalendarWithYearMonthDayBuilder();
        }
        public CalendarMonth build() {
            return CalendarMonth.yearMonth(2007, 6);
        }
        public CalendarMonth buildLenient() {
            return CalendarMonth.yearMonth(2007, 6);
        }
    }
    public static class CalendarWithYearMonthDayBuilder {
        public CalendarDay build() {
            return CalendarDay.yearMonthDay(2007, 6, 1);
        }
        public CalendarDay buildLenient() {
            return CalendarDay.yearMonthDay(2007, 6, 1);
        }
    }
}
