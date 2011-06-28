/*
 * Copyright (c) 2007-2011, Stephen Colebourne & Michael Nascimento Santos
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

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import javax.time.calendar.LocalDateTime;
import javax.time.calendar.LocalTime;
import javax.time.calendar.ZoneId;
import javax.time.calendar.ZoneResolvers;
import javax.time.calendar.ZonedDateTime;
import javax.time.calendar.format.DateTimeFormatter;
import javax.time.calendar.format.DateTimeFormatters;

/**
 * Test Performance.
 *
 * @author Stephen Colebourne
 */
public class Performance {

    /** Size. */
    private static final NumberFormat NF = NumberFormat.getIntegerInstance();
    static {
        NF.setGroupingUsed(true);
    }
    /** Size. */
    private static final int SIZE = 100000;

    /**
     * Main.
     * @param args  the arguments
     */
    public static void main(String[] args) {
        for (int i = 0; i < 5; i++) {
            System.out.println("-------------------------------------");
            process();
        }
    }
    public static void process() {
        LocalTime time = LocalTime.of(12, 30, 20);
        System.out.println(time);
        
        List<LocalDateTime> ldt = setupDateTime();
        queryListDateTime(ldt);
        formatListDateTime(ldt);
        sortListDateTime(ldt);

        List<ZonedDateTime> zdt = setupZonedDateTime();
        queryListZonedDateTime(zdt);
        formatListZonedDateTime(zdt);
        sortListZonedDateTime(zdt);

        List<Instant> instants = setupInstant();
        queryListInstant(instants);
        formatListInstant(instants);
        sortListInstant(instants);

        List<Date> judates = setupDate();
        queryListDate(judates);
        formatListDate(judates);
        sortListDate(judates);

//        List<LocalTime> times = setupTime();
//        List<LocalDate> dates = setupDate();
//        sortList(times);
//        queryList(times);
//        formatList(dates);

        List<GregorianCalendar> gcals = setupGCal();
        queryListGCal(gcals);
        formatListGCal(gcals);
        sortListGCal(gcals);
        
    }

    //-----------------------------------------------------------------------
    private static List<LocalDateTime> setupDateTime() {
        Random random = new Random(47658758756875687L);
        List<LocalDateTime> list = new ArrayList<LocalDateTime>(SIZE);
        long start = System.nanoTime();
        for (int i = 0; i < SIZE; i++) {
            LocalDateTime t = LocalDateTime.of(
                    random.nextInt(10000), random.nextInt(12) + 1, random.nextInt(28) + 1,
                    random.nextInt(24), random.nextInt(60), random.nextInt(60));
            list.add(t);
        }
        long end = System.nanoTime();
        System.out.println("LocalDT:   Setup:  " + NF.format(end - start) + " ns");
        return list;
    }

    private static void sortListDateTime(List<LocalDateTime> list) {
        long start = System.nanoTime();
        Collections.sort(list);
        long end = System.nanoTime();
        System.out.println("LocalDT:   Sort:   " + NF.format(end - start) + " ns");
    }

    private static void queryListDateTime(List<LocalDateTime> list) {
        long total = 0;
        long start = System.nanoTime();
        for (LocalDateTime dt : list) {
            total += dt.getYear();
            total += dt.getMonthOfYear().getValue();
            total += dt.getDayOfMonth();
            total += dt.getHourOfDay();
            total += dt.getMinuteOfHour();
            total += dt.getSecondOfMinute();
        }
        long end = System.nanoTime();
        System.out.println("LocalDT:   Query:  " + NF.format(end - start) + " ns" + " " + total);
    }

    private static void formatListDateTime(List<LocalDateTime> list) {
        StringBuilder buf = new StringBuilder();
        DateTimeFormatter format = DateTimeFormatters.isoDate().withLocale(Locale.ENGLISH);
        long start = System.nanoTime();
        for (LocalDateTime dt : list) {
            buf.setLength(0);
            buf.append(format.print(dt));
        }
        long end = System.nanoTime();
        System.out.println("LocalDT:   Format: " + NF.format(end - start) + " ns" + " " + buf);
    }

    //-----------------------------------------------------------------------
    private static List<ZonedDateTime> setupZonedDateTime() {
        ZoneId tz = ZoneId.of("Europe/London");
        Random random = new Random(47658758756875687L);
        List<ZonedDateTime> list = new ArrayList<ZonedDateTime>(SIZE);
        long start = System.nanoTime();
        for (int i = 0; i < SIZE; i++) {
            ZonedDateTime t = ZonedDateTime.of(
                    2008/*random.nextInt(10000)*/, random.nextInt(12) + 1, random.nextInt(28) + 1,
                    random.nextInt(24), random.nextInt(60), random.nextInt(60), 0,
                    tz, ZoneResolvers.postTransition());
            list.add(t);
        }
        long end = System.nanoTime();
        System.out.println("ZonedDT:   Setup:  " + NF.format(end - start) + " ns");
        return list;
    }

    private static void sortListZonedDateTime(List<ZonedDateTime> list) {
        long start = System.nanoTime();
        Collections.sort(list);
        long end = System.nanoTime();
        System.out.println("ZonedDT:   Sort:   " + NF.format(end - start) + " ns");
    }

    private static void queryListZonedDateTime(List<ZonedDateTime> list) {
        long total = 0;
        long start = System.nanoTime();
        for (ZonedDateTime dt : list) {
            total += dt.getYear();
            total += dt.getMonthOfYear().getValue();
            total += dt.getDayOfMonth();
            total += dt.getHourOfDay();
            total += dt.getMinuteOfHour();
            total += dt.getSecondOfMinute();
        }
        long end = System.nanoTime();
        System.out.println("ZonedDT:   Query:  " + NF.format(end - start) + " ns" + " " + total);
    }

    private static void formatListZonedDateTime(List<ZonedDateTime> list) {
        StringBuilder buf = new StringBuilder();
        DateTimeFormatter format = DateTimeFormatters.isoDate().withLocale(Locale.ENGLISH);
        long start = System.nanoTime();
        for (ZonedDateTime dt : list) {
            buf.setLength(0);
            buf.append(format.print(dt));
        }
        long end = System.nanoTime();
        System.out.println("ZonedDT:   Format: " + NF.format(end - start) + " ns" + " " + buf);
    }

    //-----------------------------------------------------------------------
    private static List<Instant> setupInstant() {
        Random random = new Random(47658758756875687L);
        List<Instant> list = new ArrayList<Instant>(SIZE);
        long start = System.nanoTime();
        for (int i = 0; i < SIZE; i++) {
            Instant t = Instant.ofEpochMilli(random.nextLong());
            list.add(t);
        }
        long end = System.nanoTime();
        System.out.println("Instant:   Setup:  " + NF.format(end - start) + " ns");
        return list;
    }

    private static void sortListInstant(List<Instant> list) {
        long start = System.nanoTime();
        Collections.sort(list);
        long end = System.nanoTime();
        System.out.println("Instant:   Sort:   " + NF.format(end - start) + " ns");
    }

    private static void queryListInstant(List<Instant> list) {
        long total = 0;
        long start = System.nanoTime();
        for (Instant dt : list) {
            total += dt.getEpochSecond();
            total += dt.getNanoOfSecond();
        }
        long end = System.nanoTime();
        System.out.println("Instant:   Query:  " + NF.format(end - start) + " ns" + " " + total);
    }

    private static void formatListInstant(List<Instant> list) {
        StringBuilder buf = new StringBuilder();
        long start = System.nanoTime();
        for (Instant dt : list) {
            buf.setLength(0);
            buf.append(dt.toString());
        }
        long end = System.nanoTime();
        System.out.println("Instant:   Format: " + NF.format(end - start) + " ns" + " " + buf);
    }

    //-----------------------------------------------------------------------
    private static List<Date> setupDate() {
        Random random = new Random(47658758756875687L);
        List<Date> list = new ArrayList<Date>(SIZE);
        long start = System.nanoTime();
        for (int i = 0; i < SIZE; i++) {
            Date t = new Date(random.nextLong());
            list.add(t);
        }
        long end = System.nanoTime();
        System.out.println("Date:      Setup:  " + NF.format(end - start) + " ns");
        return list;
    }

    private static void sortListDate(List<Date> list) {
        long start = System.nanoTime();
        Collections.sort(list);
        long end = System.nanoTime();
        System.out.println("Date:      Sort:   " + NF.format(end - start) + " ns");
    }

    private static void queryListDate(List<Date> list) {
        long total = 0;
        long start = System.nanoTime();
        for (Date dt : list) {
            total += dt.getTime();
        }
        long end = System.nanoTime();
        System.out.println("Date:      Query:  " + NF.format(end - start) + " ns" + " " + total);
    }

    private static void formatListDate(List<Date> list) {
        StringBuilder buf = new StringBuilder();
        long start = System.nanoTime();
        for (Date dt : list) {
            buf.setLength(0);
            buf.append(dt.toString());
        }
        long end = System.nanoTime();
        System.out.println("Date:      Format: " + NF.format(end - start) + " ns" + " " + buf);
    }

//    //-----------------------------------------------------------------------
//    private static List<LocalTime> setupTime() {
//        Random random = new Random(47658758756875687L);
//        List<LocalTime> list = new ArrayList<LocalTime>(SIZE);
//        long start = System.nanoTime();
//        for (int i = 0; i < SIZE; i++) {
//            LocalTime t = time(random.nextInt(24), random.nextInt(60), random.nextInt(60));
//            list.add(t);
//        }
//        long end = System.nanoTime();
//        System.out.println((end - start) + " ns");
//        return list;
//    }
//
//    private static List<LocalDate> setupDate() {
//        Random random = new Random(47658758756875687L);
//        List<LocalDate> list = new ArrayList<LocalDate>(SIZE);
//        long start = System.nanoTime();
//        for (int i = 0; i < SIZE; i++) {
//            LocalDate t = date(random.nextInt(10000), random.nextInt(12) + 1, random.nextInt(28) + 1);
//            list.add(t);
//        }
//        long end = System.nanoTime();
//        System.out.println((end - start) + " ns");
//        return list;
//    }
//
//    private static void sortList(List<LocalTime> list) {
//        long start = System.nanoTime();
//        Collections.sort(list);
//        long end = System.nanoTime();
//        System.out.println("LocalTime: Sort:   " + NF.format(end - start) + " ns");
//    }
//
//    private static void queryList(List<LocalTime> list) {
//        long total = 0;
//        long start = System.nanoTime();
//        for (LocalTime localTime : list) {
//            total += localTime.getHourOfDay().getValue();
//            total += localTime.getMinuteOfHour().getValue();
//            total += localTime.getSecondOfMinute().getValue();
//        }
//        long end = System.nanoTime();
//        System.out.println("LocalTime: Query:  " + NF.format(end - start) + " ns" + " " + total);
//    }
//
//    private static void formatList(List<LocalDate> list) {
//        StringBuilder buf = new StringBuilder();
//        DateTimeFormatter format = DateTimeFormatters.isoDate().withLocale(Locale.ENGLISH);
//        long start = System.nanoTime();
//        for (LocalDate date : list) {
//            buf.setLength(0);
//            buf.append(format.print(date));
//        }
//        long end = System.nanoTime();
//        System.out.println("LocalDate: Format: " + NF.format(end - start) + " ns" + " " + buf);
//    }

    //-----------------------------------------------------------------------
    private static List<GregorianCalendar> setupGCal() {
        java.util.TimeZone tz = java.util.TimeZone.getTimeZone("Europe/London");
        Random random = new Random(47658758756875687L);
        List<GregorianCalendar> list = new ArrayList<GregorianCalendar>(SIZE);
        long start = System.nanoTime();
        for (int i = 0; i < SIZE; i++) {
            GregorianCalendar t = new GregorianCalendar(tz);
            t.setGregorianChange(new Date(Long.MIN_VALUE));
            t.set(random.nextInt(10000), random.nextInt(12), random.nextInt(28) + 1, random.nextInt(24), random.nextInt(60), random.nextInt(60));
            list.add(t);
        }
        long end = System.nanoTime();
        System.out.println("GCalendar: Setup:  " + NF.format(end - start) + " ns");
        return list;
    }

    private static void sortListGCal(List<GregorianCalendar> list) {
        long start = System.nanoTime();
        Collections.sort(list);
        long end = System.nanoTime();
        System.out.println("GCalendar: Sort:   " + NF.format(end - start) + " ns");
    }

    private static void queryListGCal(List<GregorianCalendar> list) {
        long total = 0;
        long start = System.nanoTime();
        for (GregorianCalendar gcal : list) {
            total += gcal.get(Calendar.YEAR);
            total += gcal.get(Calendar.MONTH + 1);
            total += gcal.get(Calendar.DAY_OF_MONTH);
            total += gcal.get(Calendar.HOUR_OF_DAY);
            total += gcal.get(Calendar.MINUTE);
            total += gcal.get(Calendar.SECOND);
        }
        long end = System.nanoTime();
        System.out.println("GCalendar: Query:  " + NF.format(end - start) + " ns" + " " + total);
    }

    private static void formatListGCal(List<GregorianCalendar> list) {
        StringBuilder buf = new StringBuilder();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        long start = System.nanoTime();
        for (GregorianCalendar gcal : list) {
            buf.setLength(0);
            buf.append(format.format(gcal.getTime()));
        }
        long end = System.nanoTime();
        System.out.println("GCalendar: Format: " + NF.format(end - start) + " ns" + " " + buf);
    }

}
