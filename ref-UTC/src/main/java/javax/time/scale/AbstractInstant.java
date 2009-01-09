package javax.time.scale;

import javax.time.TimeScale;
import javax.time.Instant;

/** An instant in time on a time scale.
 * Should this support Duration operations?
 * Should we implement Comparable<AbstractInstant> here
 * @author Mark Thornton
 */
public abstract class AbstractInstant {
    static long SECONDS_PER_DAY = 86400L;

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

    private static void formatDate(long date, StringBuilder buffer) {
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

    private static void formatTime(int seconds, int leapSecond, int nanoOfSecond, StringBuilder buffer) {
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

    public abstract TimeScale getScale();

    /** Seconds since 1970-01-01.
     * In some time scales this may include any leap seconds.
     * @return seconds since the epoch
     */
    public abstract long getEpochSeconds();

    public abstract int getNanoOfSecond();

    /** Seconds since 1970-01-01 without leap seconds.
     * @return seconds since the epoch
     */
    public abstract long getSimpleEpochSeconds();

    /** leap second after simple epoch instant.
     * @return 1 during a positive leap second.
     */
    public abstract int getLeapSecond();

    /**
     * A string representation of this Instant using ISO-8601 representation.
     * <p>
     * The format of the returned string will be <code>yyyy-MM-ddTHH:mm:ss.SSSSSSSSSZ</code>.
     * If the TimeScale is not the default, then <code>[scale]</code> will be appended in place of 'Z'.
     *
     * @return an ISO-8601 representation of this Instant
     */
    public String toString() {
        long s = getSimpleEpochSeconds();
        long date = s/SECONDS_PER_DAY;
        int time = (int)(s-(date*SECONDS_PER_DAY));
        if (time < 0) {
            date--;
            time += SECONDS_PER_DAY;
        }
        // date: days since 1 Jan 1970
        StringBuilder buffer = new StringBuilder();
        formatDate(date, buffer);
        buffer.append('T');
        formatTime(time, getLeapSecond(), getNanoOfSecond(), buffer);
        if (getScale() != Instant.SCALE) {
            buffer.append('[');
            buffer.append(getScale().getName());
            buffer.append(']');
        }
        else
            buffer.append('Z');
        return buffer.toString();
    }
}
