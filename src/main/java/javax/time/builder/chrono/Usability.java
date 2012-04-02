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
package javax.time.builder.chrono;

import javax.time.DayOfWeek;
import javax.time.LocalDate;
import javax.time.calendrical.DateAdjusters;
import javax.time.i18n.CopticDate;

/**
 * Usability class for package.
 */
public final class Usability {

    public static void main(String[] args) {
        oldPackage();
        System.out.println("------");
        newPackage();
        System.out.println("------");
        quarter();
    }

    private Usability() {
    }

    private static void oldPackage() {
        CopticDate view = CopticDate.of(LocalDate.now());
        System.out.println(view);
        
        view = view.withDayOfMonth(1);
        System.out.println(view);
        
        int month = view.getMonthOfYear();
        view = CopticDate.of(view.toLocalDate().with(DateAdjusters.previousOrCurrent(DayOfWeek.MONDAY)));
        System.out.println(view);
        
        while (view.getMonthOfYear() <= month) {
            String row = "";
            for (int i = 0; i < 7; i++) {
                row += view.getDayOfMonth() + " ";
                view = view.plusDays(1);
            }
            System.out.println(row);
        }
    }

    private static void newPackage() {
//        ChronoDate<?> view = ChronoDate.now(CopticChrono.INSTANCE);
//        System.out.println(view);
//        
//        view = view.withDayOfMonth(1);
//        System.out.println(view);
//        
//        int month = view.getMonthOfYear();
//        view = view.withDayOfWeek(1);
//        System.out.println(view);
//        
//        while (view.getMonthOfYear() <= month) {
//            String row = "";
//            for (int i = 0; i < 7; i++) {
//                row += view.getDayOfMonth() + " ";
//                view = view.plusDays(1);
//            }
//            System.out.println(row);
//        }
    }

    private static void quarter() {
//        ChronoDate<?> view = ChronoDate.now(ISOChrono.INSTANCE);
//        System.out.println(view.get(QuarterYearField.QUARTER_OF_YEAR));
//        System.out.println(view.get(QuarterYearField.MONTH_OF_QUARTER));
//        System.out.println(view.get(QuarterYearField.DAY_OF_QUARTER));
//        view = view.with(QuarterYearField.QUARTER_OF_YEAR, 2);
//        System.out.println(view);
//        view = view.withDate(view.getChronology().setDateLenient(view.getDate(), QuarterYearField.QUARTER_OF_YEAR, 5));
//        System.out.println(view);
    }

}
