/*
 * Copyright (c) 2007-2012, Stephen Colebourne & Michael Nascimento Santos
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

import static javax.time.DayOfWeek.FRIDAY;
import static javax.time.DayOfWeek.MONDAY;
import static javax.time.DayOfWeek.TUESDAY;
import static javax.time.MonthOfYear.AUGUST;
import static javax.time.MonthOfYear.FEBRUARY;
import static javax.time.MonthOfYear.MARCH;
import static javax.time.Period.ofDateFields;
import static javax.time.Period.ofDays;
import static javax.time.Period.ofHours;
import static javax.time.Period.ofMinutes;
import static javax.time.Period.ofMonths;
import static javax.time.Period.ofSeconds;
import static javax.time.Period.ofYears;
import static javax.time.calendrical.DateAdjusters.dayOfWeekInMonth;
import static javax.time.calendrical.DateAdjusters.firstInMonth;
import static javax.time.calendrical.DateAdjusters.lastDayOfMonth;
import static javax.time.calendrical.DateAdjusters.next;
import static javax.time.calendrical.DateAdjusters.nextOrCurrent;
import static javax.time.calendrical.ISODateTimeRule.DAY_OF_MONTH;
import static javax.time.calendrical.ISODateTimeRule.DAY_OF_WEEK;

import javax.time.calendrical.DateTimeFields;
import javax.time.zone.ZoneOffsetTransition;

/**
 * Test class.
 * 
 * @author Stephen Colebourne
 */
public class TestFluentAPI {

    @SuppressWarnings("unused")
    public static void main(String[] args) {
        Clock clock = Clock.systemDefaultZone();
        
        LocalTime tod = clock.localTime();
        tod.plusHours(6).plusMinutes(2);
        tod.plus(ofHours(6)).plus(ofMinutes(2));
        if (AmPmOfDay.from(tod).equals(AmPmOfDay.AM)) {
            tod = tod.withHourOfDay(9);
        }
        
        LocalDate date = null;
        date = clock.today().plusDays(3);
        date = clock.today().plus(ofDays(3));
        date = Clock.systemDefaultZone().today().plus(Period.ofDays(3));
        
        date = LocalDate.of(2007, 3, 20);
        date = LocalDate.of(2007, MARCH, 20);
        date = Year.of(2007).atMonth(3).atDay(20);
        date = Year.of(2007).atMonth(MARCH).atDay(20);
        
        date = date.with(lastDayOfMonth());
        date = date.with(next(MONDAY));
        date = date.with(nextOrCurrent(MONDAY));
        date = date.with(dayOfWeekInMonth(2, TUESDAY));
        date = date.with(firstInMonth(MONDAY));
        date = date.with(Year.of(2009));
        date = date.with(MonthOfYear.of(6));
        date = date.with(AUGUST);
        
        DateTimeFields fri13 = DateTimeFields.of(
                DAY_OF_WEEK, FRIDAY.getValue(), DAY_OF_MONTH, 13);
        if (fri13.matches(date)) {
            System.out.println("Spooky");
        }
        
        // different ways to build/use periods
        date = date.plus(ofDateFields(2, 3, 1));
        date = date.plus(ofYears(3)).plus(ofMonths(2)).plus(ofDays(1));
        
        Period d2 = ofHours(2).withSeconds(3);
        Period d3 = ofHours(2).plus(ofSeconds(3));
        System.out.println(d2);
        System.out.println(d3);
        
        tod.withHourOfDay(12).withMinuteOfHour(30);
        
        QuarterOfYear q = QuarterOfYear.ofMonth(date.getMonthOfYear());
        
        MonthDay md = MonthDay.of(FEBRUARY, 4);
        md = md.with(MARCH);
        
        DAY_OF_MONTH.getValueRange().getMaximum();
        date.getMonthOfYear().maxLengthInDays();
        DAY_OF_MONTH.getValueRange(date).getMaximum();
        FEBRUARY.maxLengthInDays();
        DAY_OF_MONTH.getValueRange(FEBRUARY);
        
        DayOfWeek dow = MONDAY;
        dow = dow.next();
        dow = dow.roll(3);
//        
//        int dayIndex = day.value();
//        int dayIndex = day.value(Territory.US);
//        int dayIndex = day.valueIndexedFrom(SUNDAY);
////        SundayBasedDayOfWeek.MONDAY != DayOfWeek.MONDAY;
//        Territory.US.dayOfWeekComparator();
        
        ZoneOffset offset = ZoneOffset.ofHours(1);
        ZoneId paris = ZoneId.of("Europe/Paris");
        
        for (ZoneOffsetTransition trans : paris.getRules().getTransitions()) {
            System.out.println("Paris transition: " + trans);
        }
        System.out.println("Summer time Paris starts: " + paris.getRules().getTransitionRules().get(0));
        System.out.println("Summer time Paris ends: " + paris.getRules().getTransitionRules().get(1));
        
        LocalDateTime ldt = date.atTime(tod);
        OffsetDateTime odt = date.atTime(tod).atOffset(offset);
        ZonedDateTime zdt1 = date.atStartOfDayInZone(paris);
        ZonedDateTime zdt2 = date.atMidnight().atZone(paris);
        
        {
            Year year = Year.of(2002);
            YearMonth sixNationsMonth = year.atMonth(FEBRUARY);
            LocalDate englandWales = sixNationsMonth.atDay(12);
            LocalDate engWal = Year.of(2009).atMonth(FEBRUARY).atDay(12);
        }
        
        Clock tickingClock = Clock.tickSeconds(paris);
        for (int i = 0; i < 20; i++) {
            System.out.println(tickingClock.localTime());
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
            }
        }
    }

}
