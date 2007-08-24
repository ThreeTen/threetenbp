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

import javax.time.amount.TimeAmount;
import javax.time.field.GenericTimeField;
import javax.time.field.TimeField;
import javax.time.field.TimeView;
import javax.time.part.Day;
import javax.time.part.Forever;
import javax.time.part.Hour;
import javax.time.part.Minute;
import javax.time.part.Month;
import javax.time.part.Second;
import javax.time.part.Week;
import javax.time.part.Year;

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
    public static CalendarYear year(int year) {
        return CalendarYear.year(year);
    }

    /**
     * Gets an instance of MonthOfYear.
     */
    public static TimeField<Month, Year> monthOfYear(int monthOfYear) {
        return new GenericTimeField<Month, Year>(monthOfYear, null);
    }

    /**
     * Gets an instance of MonthOfYear.
     */
    public static TimeField<Month, Year> january() {
        return new GenericTimeField<Month, Year>(1, null);
    }

    /**
     * Gets an instance of MonthOfYear.
     */
    public static TimeField<Month, Year> february() {
        return new GenericTimeField<Month, Year>(2, null);
    }

    /**
     * Gets an instance of MonthOfYear.
     */
    public static TimeField<Month, Year> march() {
        return new GenericTimeField<Month, Year>(3, null);
    }

    /**
     * Gets an instance of DayOfMonth.
     */
    public static TimeField<Day, Month> dayOfMonth(int dayOfMonth) {
        return new GenericTimeField<Day, Month>(dayOfMonth, null);
    }

    /**
     * Gets an instance of HourOfDay.
     */
    public static TimeField<Hour, Day> hourOfDay(int hourOfDay) {
        return new GenericTimeField<Hour, Day>(hourOfDay, null);
    }

    /**
     * Gets an instance of MinuteOfHour.
     */
    public static TimeField<Minute, Hour> minuteOfHour(int minuteOfHour) {
        return new GenericTimeField<Minute, Hour>(minuteOfHour, null);
    }

    /**
     * Gets an instance of SecondOfMinute.
     */
    public static TimeField<Second, Minute> secondOfMinute(int secondOfMinute) {
        return new GenericTimeField<Second, Minute>(secondOfMinute, null);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets an instance of Years.
     */
    public static TimeAmount<Year> years(int years) {
        return TimeAmount.timeAmount(years, null);
    }

    /**
     * Gets an instance of Months.
     */
    public static TimeAmount<Month> months(int months) {
        return TimeAmount.timeAmount(months, null);
    }

    /**
     * Gets an instance of Weeks.
     */
    public static TimeAmount<Week> weeks(int weeks) {
        return TimeAmount.timeAmount(weeks, null);
    }

    /**
     * Gets an instance of Days.
     */
    public static TimeAmount<Day> days(int days) {
        return TimeAmount.timeAmount(days, null);
    }

    /**
     * Gets an instance of Hours.
     */
    public static TimeAmount<Hour> hours(int hours) {
        return TimeAmount.timeAmount(hours, null);
    }

    /**
     * Gets an instance of Minutes.
     */
    public static TimeAmount<Minute> minutes(int minutes) {
        return TimeAmount.timeAmount(minutes, null);
    }

    /**
     * Gets an instance of Seconds.
     */
    public static TimeAmount<Second> seconds(int seconds) {
        return TimeAmount.timeAmount(seconds, null);
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
    public static CalendarDay calendarDay(TimeView<Year, Forever> year, TimeView<Month, Year> monthOfYear, TimeView<Day, Month> dayOfMonth) {
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
