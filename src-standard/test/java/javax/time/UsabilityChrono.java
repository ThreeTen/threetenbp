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

import java.io.PrintStream;
import java.util.Set;
import static javax.time.calendrical.LocalDateTimeField.DAY_OF_MONTH;
import static javax.time.calendrical.LocalDateTimeField.DAY_OF_WEEK;
import static javax.time.calendrical.LocalDateTimeField.EPOCH_DAY;

import javax.time.calendrical.JulianDayField;
import javax.time.calendrical.LocalDateTimeField;
import javax.time.calendrical.LocalPeriodUnit;
import javax.time.chrono.*;

/**
 * Usability class for package.
 */
public final class UsabilityChrono {

    public static void main(String[] args) {
        System.out.println("------");
        newPackagePluggable();
        System.out.println("------");
        epochDays();
        System.out.println("------");
        printMinguoCal();
        System.out.println("------");
        example1();
    }

    private UsabilityChrono() {}

    static {
        Chronology c = JapaneseChronology.INSTANCE;
        c = MinguoChronology.INSTANCE;
        c = ThaiBuddhistChronology.INSTANCE;
        c = JapaneseChronology.INSTANCE;
        c = MinguoChronology.INSTANCE;
        c = HijrahChronology.INSTANCE;
        c = ISOChronology.INSTANCE;
    }

    private static void newPackagePluggable() {
        Chronology chrono = MinguoChronology.INSTANCE;
        
        ChronoLocalDate date = chrono.now();
        System.out.printf("now: %s%n", date);
        
        date = date.with(DAY_OF_MONTH, 1);
        System.out.printf("first of month: %s%n", date);
        
        int month = (int)date.get(LocalDateTimeField.MONTH_OF_YEAR);
        date = date.with(DAY_OF_WEEK, 1);
        System.out.printf("start of first week: %s%n", date);
        
        while (date.get(LocalDateTimeField.MONTH_OF_YEAR) <= month) {
            String row = "";
            for (int i = 0; i < 7; i++) {
                row += date.get(LocalDateTimeField.DAY_OF_MONTH) + " ";
                date = date.plus(1, LocalPeriodUnit.DAYS);
            }
            System.out.println(row);
        }
    }

    private static void epochDays() {
        output(LocalDate.now());
        output(LocalDate.of(1945, 11, 12));
        output(LocalDate.of(-4713, 11, 24));
        output(LocalDate.of(1858, 11, 17));
        output(LocalDate.of(1970, 1, 1));
        output(LocalDate.of(1, 1, 1));
    }

    protected static void output(LocalDate date) {
        System.out.println(date);
        System.out.println("EPOCH_DAY " + date.getLong(EPOCH_DAY));
        System.out.println("JDN " + date.getLong(JulianDayField.JULIAN_DAY));
        System.out.println("MJD " + date.getLong(JulianDayField.MODIFIED_JULIAN_DAY));
        System.out.println("RD  " + date.getLong(JulianDayField.RATA_DIE));
        System.out.println();
    }


    /**
     * Example code used in the ChronoDate and package.html of javax.microedition.chrono.
     */
    static void example1() {
        System.out.printf("Available Calendars%n");

        // Print the Minguo date
        ChronoLocalDate<MinguoChronology> now1 = MinguoChronology.INSTANCE.now();
        int day = now1.get(LocalDateTimeField.DAY_OF_MONTH);
        int dow = now1.get(LocalDateTimeField.DAY_OF_WEEK);
        int month = now1.get(LocalDateTimeField.MONTH_OF_YEAR);
        int year = now1.get(LocalDateTimeField.YEAR);
        System.out.printf("  Today is %s %s %d-%s-%d%n", now1.getChronology().getId(),
                dow, day, month, year);

        // Enumerate the list of available calendars and print today for each
        Set<String> names = Chronology.getAvailableIds();
        for (String name : names) {
            Chronology<?> chrono = Chronology.of(name);
            ChronoLocalDate<?> date = chrono.now();
            System.out.printf("   %20s: %s%n", chrono.getId(), date.toString());
        }

        // Print today's date and the last day of the year for the Minguo Calendar.
        ChronoLocalDate<MinguoChronology> first = now1
                .with(LocalDateTimeField.DAY_OF_MONTH, 1)
                .with(LocalDateTimeField.MONTH_OF_YEAR, 1);
        ChronoLocalDate<MinguoChronology> last = first
                .plus(1, LocalPeriodUnit.YEARS)
                .minus(1, LocalPeriodUnit.DAYS);
        System.out.printf("  %s: 1st of year: %s; end of year: %s%n", last.getChronology().getId(),
                first, last);
    }

    /**
     * Prints a Minguo calendar for the current month.
     */
    private static void printMinguoCal() {
        String chronoName = "Minguo";
        Chronology chrono = Chronology.of(chronoName);
        ChronoLocalDate today = chrono.now();
        printMonthCal(today, System.out);
    }

    /**
     * Print a month calendar with complete week rows.
     * @param date A date in some calendar
     * @param out a PrintStream
     */
    private static void printMonthCal(ChronoLocalDate date, PrintStream out) {

        int lengthOfMonth = (int)date.lengthOfMonth();
        ChronoLocalDate end = date.with(LocalDateTimeField.DAY_OF_MONTH, lengthOfMonth);
        end = end.plus(7 - end.get(LocalDateTimeField.DAY_OF_WEEK), LocalPeriodUnit.DAYS);
        // Back up to the beginning of the week including the 1st of the month
        ChronoLocalDate start = date.with(LocalDateTimeField.DAY_OF_MONTH, 1);
        start = start.minus(start.get(LocalDateTimeField.DAY_OF_WEEK), LocalPeriodUnit.DAYS);

        out.printf("%9s Month %2d, %4d%n", date.getChronology().getId(),
                date.get(LocalDateTimeField.MONTH_OF_YEAR),
                date.get(LocalDateTimeField.YEAR));
        String[] colText = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        printMonthRow(colText, " ", out);

        String[] cell = new String[7];
        for (; start.compareTo(end) <= 0; start = start.plus(1, LocalPeriodUnit.DAYS)) {
            int ndx = start.get(LocalDateTimeField.DAY_OF_WEEK) - 1;
            cell[ndx] = Integer.toString((int)start.get(LocalDateTimeField.DAY_OF_MONTH));
            if (ndx == 6) {
                printMonthRow(cell, "|", out);
            }
        }
    }

    private static void printMonthRow(String[] cells, String delim, PrintStream out) {
        for (int i = 0; i < cells.length; i++) {
            out.printf("%s%3s ", delim, cells[i]);
        }
        out.println(delim);
    }


}
