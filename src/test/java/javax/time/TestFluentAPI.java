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

import static javax.time.calendar.Calendars.*;
import static javax.time.calendar.field.DayOfWeek.*;
import static javax.time.period.Periods.*;

import javax.time.calendar.CalendarDT;
import javax.time.calendar.CalendarDate;
import javax.time.calendar.CalendarDateTime;
import javax.time.calendar.TimeHM;
import javax.time.calendar.TimeOfDay;
import javax.time.calendar.field.DayOfMonth;
import javax.time.calendar.field.DayOfWeek;
import javax.time.calendar.field.Era;
import javax.time.period.Period;
import javax.time.period.field.Days;

/**
 * Test class.
 * 
 * @author Stephen Colebourne
 */
public class TestFluentAPI {

    public static void main(String[] args) {
        TimeOfDay tod = now().currentTime();
        tod.plusHours(6).plusMinutes(2);
        tod.plus(hours(6), minutes(2));
        
        CalendarDate date = null;
        date = now().today().plusDays(3);
        date = now().today().plus(days(3));
        date = now().today().plus(Days.days(3));
        
        date = calendarDate(2007, 3, 20);
        date = calendarDate(year(Era.AD, 2007), march(), dayOfMonth(20));
        date = calendar().year(2007).december().dayOfMonth(20).buildLenient();
        date = calendar().year(1972).december().dayOfMonth(3).build();
        date = calendar().currentYear().december().dayOfMonth(20).buildLenient();
        date = calendar().zoneID("Europe/London").year(2007).august().dayOfMonth(2).build();
        
        date = calendar().zoneID("America/New_York").year(2007).march().dayOfMonth(20).buildLenient();
        date = calendar().defaultZone().year(2007).march().dayOfMonth(20).buildLenient();
        
        date = CalendarDate.calendarDate(now().currentMonth(), dayOfMonth(6));
        
        // different ways to build/use periods
        date = date.plus(yearsMonthsDays(2, 3, 1));
//        date = date.plus(3, YEARS).plus(2, MONTHS).plus(1, DAYS);
        date = date.plus(years(3), months(2), days(1));
        Period d1 = periodBuilder().hours(2).seconds(3).build();
        Period d2 = hours(2).withSeconds(3);
        Period d3 = Period.periodOf(hours(2), seconds(3));
        
        tod.with(hourOfDay(12), minuteOfHour(30));
        tod.withHourOfDay(12).withMinuteOfHour(30);
        //int q = date.get(QuarterOfYear.class);
        //int hourOfDay = HourOfDay.of(tod).get();
        
        CalendarDateTime dt = CalendarDateTime.calendarDateTime(2007, february(), 21, 12, 30);
        int sec = dt.withPrecisionAtLeastSeconds().getSecondOfMinute();
        
        CalendarDT<TimeHM> dtime = CalendarDT.calendarDateTime(2007, february(), 21, 12, 30);
        int min = dtime.time().getMinuteOfHour();
        
        DayOfMonth.RULE.getMaximumValue();
        DayOfMonth.RULE.getMaximumValue(date);
        DayOfMonth.RULE.getMaximumValue(february());
        
        DayOfWeek dow = MONDAY;
        dow = dow.next();
        dow = dow.plusDays(3);
//        dow = dow.plusDaysSkipping(3, SATURDAY, SUNDAY);
        
//        day = day.plusSkipping(days(3), WEDNESDAY_PM, SATURDAY, SUNDAY, FOURTH_JULY, XMAS_BREAK);
//        
//        int dayIndex = day.value();
//        int dayIndex = day.value(Territory.US);
//        int dayIndex = day.valueIndexedFrom(SUNDAY);
////        SundayBasedDayOfWeek.MONDAY != DayOfWeek.MONDAY;
//        Territory.US.dayOfWeekComparator();
//        
//        date.dayOfMonth().value();
        
//        {
//            Days days = days(3);
//            Seconds secs = days.convertTo(Seconds.class);
//            int amount = secs.getAmount();
//        }
//        {
//            Duration days = days(3);
//            int amount = days.getTotalAmount(SECOND);
//        }
//        {
//            Measure<Integer, Duration> days = days(3);
//            Measure<Integer, Duration> secs = days.to(SECOND);
//            int secs = days.intValue(SECOND);
//            secs.toString();
//        }
        d1 = d2;
        d2 = d1;
        d1 = d3;
        secondOfMinute(sec);
        minuteOfHour(min);
    }

}
