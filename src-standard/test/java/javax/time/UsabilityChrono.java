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

import javax.time.calendrical.DateAdjusters;
import javax.time.chrono.ChronoDate;
import javax.time.chrono.ChronoField;
import javax.time.chrono.CopticChrono;
import javax.time.i18n.CopticDate;

/**
 * Usability class for package.
 */
public final class UsabilityChrono {

    public static void main(String[] args) {
        oldPackageSpecific();
        System.out.println("------");
        newPackageSpecific();
        System.out.println("------");
        newPackagePluggable();
    }

    private UsabilityChrono() {
    }

    private static void oldPackageSpecific() {
        CopticDate date = CopticDate.of(LocalDate.now());
        System.out.println(date);
        
        date = date.withDayOfMonth(1);
        System.out.println(date);
        
        int month = date.getMonthOfYear();
        date = CopticDate.of(date.toLocalDate().with(DateAdjusters.previousOrCurrent(DayOfWeek.MONDAY)));
        System.out.println(date);
        
        while (date.getMonthOfYear() <= month) {
            String row = "";
            for (int i = 0; i < 7; i++) {
                row += date.getDayOfMonth() + " ";
                date = date.plusDays(1);
            }
            System.out.println(row);
        }
    }

    private static void newPackageSpecific() {
        javax.time.chrono.CopticDate date = CopticChrono.INSTANCE.now();
        System.out.println(date);
        
        date = date.with(ChronoField.DAY_OF_MONTH, 1);
        System.out.println(date);
        
        int month = date.getMonthOfYear();
        date = date.with(ChronoField.DAY_OF_WEEK, 1);
        System.out.println(date);
        
        while (date.getMonthOfYear() <= month) {
            String row = "";
            for (int i = 0; i < 7; i++) {
                row += date.getDayOfMonth() + " ";
                date = date.plusDays(1);
            }
            System.out.println(row);
        }
    }

    private static void newPackagePluggable() {
        CopticChrono chrono = CopticChrono.INSTANCE;
        
        ChronoDate date = chrono.now();
        System.out.println(date);
        
        date = date.with(ChronoField.DAY_OF_MONTH, 1);
        System.out.println(date);
        
        int month = date.getMonthOfYear();
        date = date.with(ChronoField.DAY_OF_WEEK, 1);
        System.out.println(date);
        
        while (date.getMonthOfYear() <= month) {
            String row = "";
            for (int i = 0; i < 7; i++) {
                row += date.getDayOfMonth() + " ";
                date = date.plusDays(1);
            }
            System.out.println(row);
        }
    }

}
