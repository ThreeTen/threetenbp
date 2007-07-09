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

import static javax.time.DayOfWeek.*;
import static javax.time.Moments.*;

/**
 * Test class.
 * 
 * @author Stephen Colebourne
 */
public class TestFluentAPI {

    public static void main(String[] args) {
        TimeOfDay tod = currentTime();
        tod.plusHours(6).plusMinutes(2);
        tod.plus(hours(6), minutes(2));
        
        CalendarDay date = null;
        date = today().plusDays(3);
        date = today().plus(days(3));
        date = Now.today().plus(Days.days(3));
        
        date = calendarDay(2007, 3, 20);
        date = calendarDay(year(2007), march(), dayOfMonth(20));
        date = moment().year(2007).december().dayOfMonth(20).resolveLenient();
        date = moment().year(1972).december().dayOfMonth(3).resolve();
        date = moment().currentYear().december().dayOfMonth(20).resolveLenient();
        date = moment().zoneID("Europe/London").year(2007).august().dayOfMonth(2).resolve();
        
        date = moment().zoneID("America/New_York").year(2007).march().dayOfMonth(20).resolveLenient();
        date = moment().defaultZone().year(2007).march().dayOfMonth(20).resolveLenient();
        
        date = CalendarDay.calendarDay(currentMonth(), dayOfMonth(6));
        
        tod.with(hourOfDay(12), minuteOfHour(30));
        tod.withHourOfDay(12).withMinuteOfHour(30);
        //int q = date.get(QuarterOfYear.class);
        //int hourOfDay = HourOfDay.of(tod).get();
        
        DayOfWeek dow = MONDAY;
        dow = dow.next();
        dow = dow.plusDays(3);
        dow = dow.plusDaysSkipping(3, SATURDAY, SUNDAY);
        
//        day = day.plusSkipping(days(3), WEDNESDAY_PM, SATURDAY, SUNDAY, FOURTH_JULY, XMAS_BREAK);
//        
//        int dayIndex = day.value();
//        int dayIndex = day.value(Territory.US);
//        int dayIndex = day.valueIndexedFrom(SUNDAY);
////        SundayBasedDayOfWeek.MONDAY != DayOfWeek.MONDAY;
//        Territory.US.dayOfWeekComparator();
//        
//        date.dayOfMonth().value();
    }

}
