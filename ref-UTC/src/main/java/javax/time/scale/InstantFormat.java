package javax.time.scale;

import javax.time.Instant;
import javax.time.InstantProvider;
import java.text.Format;
import java.text.FieldPosition;
import java.text.ParsePosition;

/** Simple formatting of Instant's
 */
public class InstantFormat extends Format {
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

    private static InstantFormat INSTANCE = new InstantFormat();

    public static InstantFormat getInstance() {
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

    public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
        if (obj instanceof Instant)
            return format((Instant)obj, toAppendTo, pos);
        if (obj instanceof InstantProvider)
            return format(((InstantProvider)obj).toInstant(), toAppendTo, pos);
        if (obj == null)
            throw new NullPointerException();
        throw new IllegalArgumentException("Can't format class as an Instant: "+obj.getClass().getName());
    }

    public StringBuffer format(Instant instant, StringBuffer toAppendTo, FieldPosition pos) {
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
        if (!instant.getScale().equals(Instant.getDefaultScale())) {
            toAppendTo.append('[');
            toAppendTo.append(instant.getScale().getName());
            toAppendTo.append(']');
        }
        else
            toAppendTo.append('Z');
        return toAppendTo;
    }

    public Object parseObject(String source, ParsePosition pos) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
