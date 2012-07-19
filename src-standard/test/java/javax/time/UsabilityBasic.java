/*
 * Copyright (c) 2012, Stephen Colebourne & Michael Nascimento Santos
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

import static javax.time.calendrical.DateTimeAdjusters.previousOrCurrent;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.time.calendrical.DateTime;
import javax.time.calendrical.DateTimeBuilder;
import javax.time.calendrical.DateTimeField;
import javax.time.calendrical.LocalDateTimeField;
import javax.time.calendrical.LocalPeriodUnit;
import javax.time.calendrical.QuarterYearField;
import javax.time.calendrical.YearMonth;
import javax.time.format.DateTimeFormatter;
import javax.time.format.DateTimeFormatterBuilder;

/**
 * Usability class for package.
 */
public final class UsabilityBasic {

    public static void main(String[] args) {
        simpleCalendar();
        System.out.println("------");
        lookup();
        System.out.println("------");
        period();
        System.out.println("------");
        resolve1();
        System.out.println("------");
        resolve2();
        System.out.println("------");
        resolve3();
        System.out.println("------");
        resolve4();
        System.out.println("------");
        print1();
        System.out.println("------");
        print2();
        System.out.println("------");
        sort();
    }

    private UsabilityBasic() {
    }

    private static void simpleCalendar() {
        LocalDate date = LocalDate.now();
        System.out.println(date);
        
        date = date.withDayOfMonth(1);
        System.out.println(date);
        
        int month = date.getMonth().getValue();
        date = date.with(previousOrCurrent(DayOfWeek.MONDAY));
        System.out.println(date);
        
        while (date.getMonth().getValue() <= month) {
            String row = "";
            for (int i = 0; i < 7; i++) {
                row += date.getDayOfMonth() + " ";
                date = date.plusDays(1);
            }
            System.out.println(row);
        }
    }

    private static void lookup() {
        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.now();
        LocalDateTime dateTime = LocalDateTime.now();
//        System.out.println(LocalDateField.DAY_OF_MONTH.getDateRules().get(date));
//        System.out.println(LocalDateField.MONTH_OF_YEAR.getDateRules().get(date));
//        System.out.println(LocalDateField.YEAR.getDateRules().get(date));
//        System.out.println(QuarterYearField.QUARTER_OF_YEAR.getDateRules().get(date));
//        System.out.println(QuarterYearField.MONTH_OF_QUARTER.getDateRules().get(date));
//        System.out.println(QuarterYearField.DAY_OF_QUARTER.getDateRules().get(date));
        
        output(date, LocalDateTimeField.DAY_OF_MONTH);
        output(date, LocalDateTimeField.MONTH_OF_YEAR);
        output(date, LocalDateTimeField.YEAR);
        output(date, QuarterYearField.QUARTER_OF_YEAR);
        output(date, QuarterYearField.MONTH_OF_QUARTER);
        output(date, QuarterYearField.DAY_OF_QUARTER);
        
        output(dateTime, LocalDateTimeField.DAY_OF_MONTH);
        output(time, LocalDateTimeField.HOUR_OF_DAY);
        output(time, LocalDateTimeField.MINUTE_OF_HOUR);
        
        DateTime cal = date;
        System.out.println("DoM: " + cal.get(LocalDateTimeField.DAY_OF_MONTH));
    }

    protected static void output(LocalDate date, DateTimeField field) {
        System.out.println(field + " " + date.get(field));
    }

    protected static void output(LocalDateTime dateTime, DateTimeField field) {
        System.out.println(field + " " + dateTime.get(field));
    }

    protected static void output(LocalTime time, DateTimeField field) {
        System.out.println(field + " " + time.get(field));
    }

    private static void period() {
        LocalDate date1 = LocalDate.now();
        LocalDate date2 = LocalDate.now().plusDays(25367);
        System.out.println(LocalPeriodUnit.DAYS.between(date1, date2));
        System.out.println(LocalPeriodUnit.YEARS.between(date1, date2));
        
        date1 = LocalDate.of(2012, 2, 20);
        date2 = LocalDate.of(2014, 2, 19);
        System.out.println(LocalPeriodUnit.YEARS.between(date1, date2));
        date2 = LocalDate.of(2014, 2, 20);
        System.out.println(LocalPeriodUnit.YEARS.between(date1, date2));
        date2 = LocalDate.of(2014, 2, 21);
        System.out.println(LocalPeriodUnit.YEARS.between(date1, date2));
        date2 = LocalDate.of(2010, 2, 19);
        System.out.println(LocalPeriodUnit.YEARS.between(date1, date2));
        date2 = LocalDate.of(2010, 2, 20);
        System.out.println(LocalPeriodUnit.YEARS.between(date1, date2));
        date2 = LocalDate.of(2010, 2, 21);
        System.out.println(LocalPeriodUnit.YEARS.between(date1, date2));
        
        LocalDate date3 = LocalDate.now().plus(3, LocalPeriodUnit.DAYS);
        System.out.println("3 days later " + date3);
    }

    private static void resolve1() {
        DateTimeBuilder builder = new DateTimeBuilder();
        builder.addFieldValue(LocalDateTimeField.YEAR, 2012);
        builder.addFieldValue(LocalDateTimeField.MONTH_OF_YEAR, 4);
        builder.addFieldValue(LocalDateTimeField.DAY_OF_MONTH, 18);
        System.out.println("Setup: " + builder);
        System.out.println("Resolved: " + builder.resolve());
        System.out.println("Date: " + LocalDate.from(builder));
    }

    private static void resolve2() {
        DateTimeBuilder builder = new DateTimeBuilder();
        builder.addFieldValue(LocalDateTimeField.YEAR, 2012);
        builder.addFieldValue(LocalDateTimeField.MONTH_OF_YEAR, 4);
        builder.addFieldValue(LocalDateTimeField.DAY_OF_MONTH, 18);
        builder.addFieldValue(LocalDateTimeField.DAY_OF_WEEK, 1);
        System.out.println("Setup: " + builder);
        try {
            builder.resolve();
        } catch (RuntimeException ex) {
            System.err.println(ex.toString());
        }
    }

    private static void resolve3() {
        DateTimeBuilder builder = new DateTimeBuilder();
        builder.addCalendrical(LocalDate.of(2012, 1, 2));
        builder.addCalendrical(OffsetDateTime.of(2012, 4, 3, 12, 30, ZoneOffset.ofHours(2)));
        System.out.println("Setup: " + builder);
        try {
            builder.resolve();
        } catch (RuntimeException ex) {
            System.err.println(ex.toString());
        }
    }

    private static void resolve4() {
        DateTimeBuilder builder = new DateTimeBuilder();
        builder.addFieldValue(LocalDateTimeField.YEAR, 2012);
        builder.addFieldValue(LocalDateTimeField.MONTH_OF_YEAR, 5);
        builder.addFieldValue(QuarterYearField.QUARTER_OF_YEAR, 2);
        builder.addFieldValue(QuarterYearField.MONTH_OF_QUARTER, 2);
        builder.addFieldValue(QuarterYearField.DAY_OF_QUARTER, 33);
        builder.addFieldValue(LocalDateTimeField.DAY_OF_MONTH, 3);
        System.out.println("Setup: " + builder);
        System.out.println("Resolved: " + builder.resolve());
        System.out.println("Date: " + LocalDate.from(builder));
    }

    private static void print1() {
        DateTimeFormatter f = new DateTimeFormatterBuilder().appendText(LocalDateTimeField.AMPM_OF_DAY)
                .appendLiteral(' ').appendValue(LocalDateTimeField.AMPM_OF_DAY).toFormatter();
        System.out.println(f.print(LocalTime.of(12, 30)));
        System.out.println(f.print(AmPm.AM));
        System.out.println(f.print(ZonedDateTime.now()));
    }

    private static void print2() {
        DateTimeFormatter f = new DateTimeFormatterBuilder().appendText(LocalDateTimeField.MONTH_OF_YEAR)
                .appendLiteral(' ').appendValue(LocalDateTimeField.YEAR).toFormatter();
        System.out.println(f.print(LocalDate.now()));
        System.out.println(f.print(YearMonth.now()));
        System.out.println(f.print(ZonedDateTime.now()));
    }

    private static void sort() {
        List<DateTime> list = Arrays.<DateTime>asList(LocalDate.now().plusMonths(3), LocalDate.now().minusMonths(3), LocalDateTime.now());
        Collections.sort(list, LocalDateTimeField.MONTH_OF_YEAR);
        System.out.println(list);
    }

}
