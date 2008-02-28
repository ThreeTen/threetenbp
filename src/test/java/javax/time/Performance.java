/*
 * Copyright (c) 2007,2008, Stephen Colebourne & Michael Nascimento Santos
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

import static javax.time.calendar.LocalTime.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;

import javax.time.calendar.LocalTime;

/**
 * Test Performance.
 *
 * @author Stephen Colebourne
 */
public class Performance {

    /** Size. */
    private static final int SIZE = 100000;

    /**
     * Main.
     * @param args  the arguments
     */
    public static void main(String[] args) {
        LocalTime time = time(12, 30, 20);
        System.out.println(time);
        
        List<LocalTime> list = setup();
        sortList(list);
        queryList(list);
//        List<GregorianCalendar> list = setupGCal();
//        sortListGCal(list);
//        queryListGCal(list);
    }

    private static List<LocalTime> setup() {
        Random random = new Random(47658758756875687L);
        List<LocalTime> list = new ArrayList<LocalTime>(SIZE);
        long start = System.nanoTime();
        for (int i = 0; i < SIZE; i++) {
            LocalTime t = time(random.nextInt(24), random.nextInt(60), random.nextInt(60));
            list.add(t);
        }
        long end = System.nanoTime();
        System.out.println((end - start) + " ns");
        return list;
    }

    private static void sortList(List<LocalTime> list) {
        long start = System.nanoTime();
        Collections.sort(list);
        long end = System.nanoTime();
        System.out.println((end - start) + " ns");
    }

    private static void queryList(List<LocalTime> list) {
        long total = 0;
        long start = System.nanoTime();
        for (LocalTime localTime : list) {
            total += localTime.getHourOfDay().getValue();
            total += localTime.getMinuteOfHour().getValue();
            total += localTime.getSecondOfMinute().getValue();
        }
        long end = System.nanoTime();
        System.out.println((end - start) + " ns" + " " + total);
    }

    private static List<GregorianCalendar> setupGCal() {
        Random random = new Random(47658758756875687L);
        List<GregorianCalendar> list = new ArrayList<GregorianCalendar>(SIZE);
        long start = System.nanoTime();
        for (int i = 0; i < SIZE; i++) {
            GregorianCalendar t = new GregorianCalendar(1970, 0, 1, random.nextInt(24), random.nextInt(60), random.nextInt(60));
            list.add(t);
        }
        long end = System.nanoTime();
        System.out.println((end - start) + " ns");
        return list;
    }

    private static void sortListGCal(List<GregorianCalendar> list) {
        long start = System.nanoTime();
        Collections.sort(list);
        long end = System.nanoTime();
        System.out.println((end - start) + " ns");
    }

    private static void queryListGCal(List<GregorianCalendar> list) {
        long total = 0;
        long start = System.nanoTime();
        for (GregorianCalendar gcal : list) {
            total += gcal.get(Calendar.HOUR_OF_DAY);
            total += gcal.get(Calendar.MINUTE);
            total += gcal.get(Calendar.SECOND);
        }
        long end = System.nanoTime();
        System.out.println((end - start) + " ns" + " " + total);
    }

}
