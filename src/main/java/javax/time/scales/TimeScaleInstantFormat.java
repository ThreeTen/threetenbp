/*
 * Copyright (c) 2009-2010, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time.scales;

import java.text.Format;
import java.text.FieldPosition;
import java.text.ParsePosition;
import javax.time.TimeScaleInstant;

/** Format TimeScaleInstant as ISO8601 String's.
 * Currently parsing is not implemented.
 * @author Mark Thornton
 */
public class TimeScaleInstantFormat extends Format {
    private static long SECONDS_PER_DAY = 86400L;

    private static int DAYS_STANDARD_YEAR =365;
    private static int DAYS_JULIAN_CYCLE = 4*DAYS_STANDARD_YEAR+1;
    private static int DAYS_STANDARD_CENTURY = 25*DAYS_JULIAN_CYCLE-1;
    private static int DAYS_GREGORIAN_CYCLE = 4*DAYS_STANDARD_CENTURY+1;

    /** days from 1600-03-01 to 1970-01-01 */
    private static int DATE_1600_03_01 = 3*DAYS_STANDARD_CENTURY + (70/4)*DAYS_JULIAN_CYCLE + (70%4)*DAYS_STANDARD_YEAR
     - (31+28);

    private static final String[] SMALL_NUMBERS;

    static
    {
        SMALL_NUMBERS = new String[62];
        char[] text = new char[2];
        text[0] = '0';
        for (int i=0; i<10; i++) {
            text[1] = (char)('0'+i);
            SMALL_NUMBERS[i] = new String(text);
        }
        for (int i=10; i<SMALL_NUMBERS.length; i++) {
            text[0] = (char)('0'+i/10);
            text[1] = (char)('0'+i%10);
            SMALL_NUMBERS[i] = new String(text);
        }

    }

    private static TimeScaleInstantFormat INSTANCE = new TimeScaleInstantFormat();

    public static TimeScaleInstantFormat getInstance() {
        return INSTANCE;
    }

    private static void formatDate(long date, StringBuffer buffer) {
        // convert to days since 1600-03-01
        date += DATE_1600_03_01;
        long year = date / DAYS_GREGORIAN_CYCLE;
        int r = (int)(date % DAYS_GREGORIAN_CYCLE);
        if (r < 0) {
            year--;
            r += DAYS_GREGORIAN_CYCLE;
        }
        year = 1600 + 400*year;

        int q = r/DAYS_STANDARD_CENTURY;
        r = r%DAYS_STANDARD_CENTURY;
        int leap = 0;
        if (q == 4) {
            q--;
            r = DAYS_STANDARD_CENTURY-1;
            leap = 1;
        }
        year += 100*q;

        q = r/DAYS_JULIAN_CYCLE;
        r = r%DAYS_JULIAN_CYCLE;
        year += 4*q;

        q = r/DAYS_STANDARD_YEAR;
        r = r%DAYS_STANDARD_YEAR;
        if (q == 4) {
            q--;
            r = DAYS_STANDARD_YEAR-1;
            leap = 1;
        }
        year += q;
        // now have the year (starting in March) plus the subsequent days
        int month = (r*5+308)/153-2;
        int day = r-(month+4)*153/5+122+leap;
        if (month >= 10) {
            year++;
            month -= 10;
        }
        else
            month += 2;
        if (year < 0) {
            buffer.append('-');
            year = -year;
        }
        if (year < 1000) {
            // pad to at least 4 digits
            buffer.append('0');
            if (year < 100) {
                buffer.append('0');
                if (year < 10)
                    buffer.append('0');
            }
        }
        buffer.append(year);
        buffer.append('-');
        buffer.append(SMALL_NUMBERS[month+1]);
        buffer.append('-');
        buffer.append(SMALL_NUMBERS[day+1]);
    }

    private static void formatTime(int seconds, int leapSecond, int nanoOfSecond, StringBuffer buffer) {
        int q = seconds / 3600;
        buffer.append(SMALL_NUMBERS[q]);
        buffer.append(':');
        seconds = seconds % 3600;
        q = seconds / 60;
        buffer.append(SMALL_NUMBERS[q]);
        seconds = seconds%60 + leapSecond;
        if (seconds > 0 || nanoOfSecond > 0) {
            buffer.append(':');
            buffer.append(SMALL_NUMBERS[seconds]);
            if (nanoOfSecond > 0) {
                buffer.append('.');
                int d = 100000000;
                do {
                    q = nanoOfSecond/d;
                    buffer.append((char)('0'+q));
                    nanoOfSecond = nanoOfSecond % d;
                    d = d/10;
                }
                while (nanoOfSecond != 0);
            }
        }
    }

    @Override
    public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
        if (obj instanceof TimeScaleInstant)
            return format((TimeScaleInstant)obj, toAppendTo, pos);
        if (obj == null)
            throw new NullPointerException();
        throw new IllegalArgumentException("Can't format class as an Instant: "+obj.getClass().getName());
    }

    public StringBuffer format(TimeScaleInstant instant, StringBuffer toAppendTo, FieldPosition pos) {
        long s = instant.getEpochSeconds();
        long date = s/SECONDS_PER_DAY;
        int time = (int)(s-(date*SECONDS_PER_DAY));
        if (time < 0) {
            date--;
            time += SECONDS_PER_DAY;
        }
        // date: days since 1 Jan 1970
        formatDate(date, toAppendTo);
        toAppendTo.append('T');
        formatTime(time, instant.getLeapSecond(), instant.getNanoOfSecond(), toAppendTo);
        toAppendTo.append('[');
        toAppendTo.append(instant.getTimeScale().getName());
        toAppendTo.append(']');
        return toAppendTo;
    }

    @Override
    public Object parseObject(String source, ParsePosition pos) {
        throw new UnsupportedOperationException("Not implemented");
    }

}
