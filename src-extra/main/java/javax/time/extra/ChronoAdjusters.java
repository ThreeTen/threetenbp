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
package javax.time.extra;

import static javax.time.calendrical.LocalPeriodUnit.MONTHS;

import javax.time.LocalDate;
import javax.time.calendrical.DateTime;
import javax.time.calendrical.DateTime.WithAdjuster;
import javax.time.calendrical.DateTimeAdjusters;
import javax.time.chrono.ChronoDate;
import javax.time.chrono.HijrahChronology;
import javax.time.chrono.MinguoChronology;

/**
 * Adjusters that allow dates to be adjusted in terms of a calendar system.
 */
public final class ChronoAdjusters {

    /**
     * Restricted constructor.
     */
    private ChronoAdjusters() {
    }

    //-----------------------------------------------------------------------
    public static WithAdjuster minguo(final WithAdjuster adjuster) {
        return new WithAdjuster() {
            @Override
            public DateTime doAdjustment(DateTime dateTime) {
                ChronoDate baseDate = MinguoChronology.INSTANCE.date(dateTime);
                ChronoDate adjustedDate = (ChronoDate) adjuster.doAdjustment(baseDate);
                return dateTime.with(adjustedDate);
            }
        };
    }

    public static WithAdjuster hijrah(final WithAdjuster adjuster) {
        return new WithAdjuster() {
            @Override
            public DateTime doAdjustment(DateTime dateTime) {
                ChronoDate baseDate = HijrahChronology.INSTANCE.date(dateTime);
                ChronoDate adjustedDate = (ChronoDate) adjuster.doAdjustment(baseDate);
                return dateTime.with(adjustedDate);
            }
        };
    }

    public static void main(String[] args) {
        LocalDate date = LocalDate.now();
        System.out.println(date);
        
//        date = date.with(hijrah(dt -> dt.plus(1, MONTHS)));
//        date = date.with(minguo(dt -> dt.plus(1, MONTHS)));
//        date = date.with(minguo(firstDayOfNextMonth()));
//        date = date.with(hijrah(firstDayOfNextMonth()));
        
        date = date.with(hijrah(new WithAdjuster() {
            @Override
            public DateTime doAdjustment(DateTime dateTime) {
                return dateTime.plus(1, MONTHS);
            }
        }));
        System.out.println(date);
        
        date = date.with(minguo(new WithAdjuster() {
            @Override
            public DateTime doAdjustment(DateTime dateTime) {
                return dateTime.plus(1, MONTHS);
            }
        }));
        System.out.println(date);
        
        date = date.with(minguo(DateTimeAdjusters.firstDayOfNextMonth()));
        System.out.println(date);
        
        date = date.with(hijrah(DateTimeAdjusters.firstDayOfNextMonth()));
        System.out.println(date);
    }

}
