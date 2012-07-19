/*
 * Copyright (c) 2008-2012, Stephen Colebourne & Michael Nascimento Santos
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

import static javax.time.Month.DECEMBER;
import static javax.time.calendrical.DateTimeAdjusters.lastDayOfMonth;
import static javax.time.calendrical.LocalPeriodUnit.MONTHS;

import javax.time.calendrical.MonthDay;
import javax.time.calendrical.Year;
import javax.time.zone.ZoneResolvers;

/**
 * Examples for this project.
 */
public class Examples {

    /**
     * Main method.
     * @param args  no arguments needed
     */
    public static void main(String[] args) {
        Clock clock = Clock.systemDefaultZone();
        
        ZonedDateTime zdt = ZonedDateTime.now(clock);
        System.out.println("Current date-time: " + zdt);
        
        ZonedDateTime zdtNewYork = ZonedDateTime.now(Clock.system(ZoneId.of("America/New_York")));
        System.out.println("Current date-time in New York: " + zdtNewYork);
        
        ZonedDateTime zdtParis = ZonedDateTime.now(Clock.system(ZoneId.of("Europe/Paris")));
        System.out.println("Current date-time in Paris: " + zdtParis);
        
        LocalDateTime ldt = LocalDateTime.now(clock);
        System.out.println("Current local date-time: " + ldt);
        
        Year year = Year.now(clock);
        System.out.println("Year: " + year.getValue());
        
        LocalDate today = LocalDate.now(clock);
        System.out.println("Today: " + today);
        
        LocalTime time = LocalTime.now(clock);
        System.out.println("Current time of day: " + time);
        
        LocalDate later = LocalDate.now(clock).plusMonths(2).plusDays(3);
        System.out.println("Two months three days after today: " + later);
        
        Period period = Period.of(3, MONTHS);
        LocalDate moreLater = LocalDate.now(clock).plus(period);
        System.out.println("Period " + period + " after today : " + moreLater);
        
        LocalDate dec = LocalDate.now(clock).with(DECEMBER);
        System.out.println("Change to same day in December: " + dec);
        
        LocalDate lastDayOfMonth = LocalDate.now(clock).with(lastDayOfMonth());
        System.out.println("Last day of month: " + lastDayOfMonth);
        
///        LocalDate tempDate = LocalDate.now(clock);
//        DateTimeFields fri13matcher = DateTimeFields.of(
//                DAY_OF_WEEK, FRIDAY.getValue(), DAY_OF_MONTH, 13);
//        boolean fri13 = fri13matcher.matches(tempDate);
//        System.out.println("Is Friday the Thirteenth: " + fri13);
        
        LocalDateTime dt = LocalDateTime.of(2008, 3, 30, 1, 30);
        System.out.println("Local date-time in Spring DST gap: " + dt);
        
        ZonedDateTime resolved = ZonedDateTime.of(dt, ZoneId.of("Europe/London"), ZoneResolvers.postTransition());
        System.out.println("...resolved to valid date-time in Europe/London: " + resolved);
        
//        String formattedRFC = DateTimeFormatters.rfc1123().print(resolved);
//        System.out.println("...printed as RFC1123: " + formattedRFC);
//        
//        DateTimeFormatter f = new DateTimeFormatterBuilder()
//            .appendValue(YEAR, 4, 10, SignStyle.ALWAYS)
//            .appendLiteral('Q')
//            .appendValue(QUARTER_OF_YEAR)
//            .appendLiteral(' ')
//            .appendText(MONTH_OF_YEAR)
//            .appendLiteral('(')
//            .appendValue(MONTH_OF_YEAR)
//            .appendLiteral(')')
//            .appendLiteral(' ')
//            .appendValue(DAY_OF_MONTH, 2)
//            .toFormatter(Locale.ENGLISH);
//        String formatted = f.print(resolved);
//        System.out.println("...printed using complex format: " + formatted);
        
        MonthDay bday = MonthDay.of(DECEMBER, 3);
        System.out.println("Brazillian birthday (no year): " + bday);
    }

}
