/*
 * Copyright (c) 2008-2011, Stephen Colebourne & Michael Nascimento Santos
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

import static javax.time.calendar.DateAdjusters.lastDayOfMonth;
import static javax.time.calendar.DayOfWeek.FRIDAY;
import static javax.time.calendar.ISOChronology.*;
import static javax.time.calendar.MonthOfYear.DECEMBER;
import static javax.time.calendar.Period.ofDateFields;

import java.util.Locale;

import javax.time.calendar.Clock;
import javax.time.calendar.DateTimeFields;
import javax.time.calendar.ISOChronology;
import javax.time.calendar.LocalDate;
import javax.time.calendar.LocalDateTime;
import javax.time.calendar.LocalTime;
import javax.time.calendar.MonthDay;
import javax.time.calendar.Period;
import javax.time.calendar.ZoneId;
import javax.time.calendar.Year;
import javax.time.calendar.ZoneResolvers;
import javax.time.calendar.ZonedDateTime;
import javax.time.calendar.format.DateTimeFormatter;
import javax.time.calendar.format.DateTimeFormatterBuilder;
import javax.time.calendar.format.DateTimeFormatters;
import javax.time.calendar.format.DateTimeFormatterBuilder.SignStyle;

/**
 * Examples for this project.
 *
 * @author Stephen Colebourne
 */
public class Examples {

    /**
     * Main method.
     * @param args  no arguments needed
     */
    public static void main(String[] args) {
        Clock clock = Clock.systemDefaultZone();
        
        ZonedDateTime zdt = clock.zonedDateTime();
        System.out.println("Current date-time: " + zdt);
        
        ZonedDateTime zdtNewYork = Clock.system(ZoneId.of("America/New_York")).zonedDateTime();
        System.out.println("Current date-time in New York: " + zdtNewYork);
        
        ZonedDateTime zdtParis = Clock.system(ZoneId.of("Europe/Paris")).zonedDateTime();
        System.out.println("Current date-time in Paris: " + zdtParis);
        
        LocalDateTime ldt = clock.dateTime();
        System.out.println("Current local date-time: " + ldt);
        
        Year year = clock.year();
        System.out.println("Year: " + year.getValue());
        
        LocalDate today = clock.today();
        System.out.println("Today: " + today);
        
        LocalDate tomorrow = clock.tomorrow();
        System.out.println("Tomorrow: " + tomorrow);
        
        LocalDate yesterday = clock.yesterday();
        System.out.println("Yesterday: " + yesterday);
        
        LocalTime time = clock.time();
        System.out.println("Current time of day: " + time);
        
        LocalDate later = clock.today().plusMonths(2).plusDays(3);
        System.out.println("Two months three days after today: " + later);
        
        Period period = ofDateFields(1, 3, 5);
        LocalDate moreLater = clock.today().plus(period);
        System.out.println("Period " + period + " after today : " + moreLater);
        
        LocalDate dec = clock.today().with(DECEMBER);
        System.out.println("Change to same day in December: " + dec);
        
        LocalDate lastDayOfMonth = clock.today().with(lastDayOfMonth());
        System.out.println("Last day of month: " + lastDayOfMonth);
        
        LocalDate tempDate = clock.today();
        DateTimeFields fri13matcher = DateTimeFields.of(
                ISOChronology.dayOfWeekRule(), FRIDAY.getValue(), ISOChronology.dayOfMonthRule(), 13);
        boolean fri13 = tempDate.matches(fri13matcher);
        System.out.println("Is Friday the Thirteenth: " + fri13);
        
        LocalDateTime dt = LocalDateTime.of(2008, 3, 30, 1, 30);
        System.out.println("Local date-time in Spring DST gap: " + dt);
        
        ZonedDateTime resolved = ZonedDateTime.of(dt, ZoneId.of("Europe/London"), ZoneResolvers.postTransition());
        System.out.println("...resolved to valid date-time in Europe/London: " + resolved);
        
        String formattedRFC = DateTimeFormatters.rfc1123().print(resolved);
        System.out.println("...printed as RFC1123: " + formattedRFC);
        
        DateTimeFormatter f = new DateTimeFormatterBuilder()
            .appendValue(yearRule(), 4, 10, SignStyle.ALWAYS)
            .appendLiteral('Q')
            .appendValue(quarterOfYearRule())
            .appendLiteral(' ')
            .appendText(monthOfYearRule())
            .appendLiteral('(')
            .appendValue(monthOfYearRule())
            .appendLiteral(')')
            .appendLiteral(' ')
            .appendValue(dayOfMonthRule(), 2)
            .toFormatter(Locale.ENGLISH);
        String formatted = f.print(resolved);
        System.out.println("...printed using complex format: " + formatted);
        
        MonthDay bday = MonthDay.of(DECEMBER, 3);
        System.out.println("Brazillian birthday (no year): " + bday);
    }

}
