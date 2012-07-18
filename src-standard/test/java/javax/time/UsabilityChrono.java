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

import static javax.time.calendrical.LocalDateTimeField.DAY_OF_MONTH;
import static javax.time.calendrical.LocalDateTimeField.DAY_OF_WEEK;
import static javax.time.calendrical.LocalDateTimeField.EPOCH_DAY;

import javax.time.calendrical.JulianDayField;
import javax.time.chrono.Chrono;
import javax.time.chrono.ChronoDate;
import javax.time.chrono.MinguoChrono;

/**
 * Usability class for package.
 */
public final class UsabilityChrono {

    public static void main(String[] args) {
        System.out.println("------");
        newPackagePluggable();
        System.out.println("------");
        epochDays();
    }

    private UsabilityChrono() {
    }

//    private static void oldPackageSpecific() {
//        CopticDate date = CopticDate.of(LocalDate.now());
//        System.out.println(date);
//        
//        date = date.withDayOfMonth(1);
//        System.out.println(date);
//        
//        int month = date.getMonth();
//        date = CopticDate.of(date.toLocalDate().with(DateAdjusters.previousOrCurrent(DayOfWeek.MONDAY)));
//        System.out.println(date);
//        
//        while (date.getMonth() <= month) {
//            String row = "";
//            for (int i = 0; i < 7; i++) {
//                row += date.getDayOfMonth() + " ";
//                date = date.plusDays(1);
//            }
//            System.out.println(row);
//        }
//    }

    private static void newPackagePluggable() {
        Chrono chrono = MinguoChrono.INSTANCE;
        
        ChronoDate date = chrono.now();
        System.out.println(date);
        
        date = date.with(DAY_OF_MONTH, 1);
        System.out.println(date);
        
        int month = date.getMonth();
        date = date.with(DAY_OF_WEEK, 1);
        System.out.println(date);
        
        while (date.getMonth() <= month) {
            String row = "";
            for (int i = 0; i < 7; i++) {
                row += date.getDayOfMonth() + " ";
                date = date.plusDays(1);
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
        System.out.println("EPOCH_DAY " + date.get(EPOCH_DAY));
        System.out.println("JDN " + date.get(JulianDayField.JULIAN_DAY));
        System.out.println("MJD " + date.get(JulianDayField.MODIFIED_JULIAN_DAY));
        System.out.println("RD  " + date.get(JulianDayField.RATA_DIE));
        System.out.println();
    }

}
