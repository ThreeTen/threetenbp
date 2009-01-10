package javax.time.scale;

import javax.time.Instant;
import javax.time.Duration;
import java.io.Serializable;

/** An instant in time on a time scale.
 * Should this support Duration operations?
 * We should also implement Comparable<AbstractInstant> here
 * @author Mark Thornton
 */
public abstract class AbstractInstant<T extends AbstractInstant> implements Serializable {
    /**
     * Constant for nanos per second.
     */
    private static final int NANOS_PER_SECOND = 1000000000;
    static long SECONDS_PER_DAY = 86400L;

    private static int DAYS_STANDARD_YEAR =365;
    private static int DAYS_JULIAN_CYCLE = 4*DAYS_STANDARD_YEAR+1;
    private static int DAYS_STANDARD_CENTURY = 25*DAYS_JULIAN_CYCLE-1;
    private static int DAYS_GREGORIAN_CYCLE = 4*DAYS_STANDARD_CENTURY+1;

    /** days from 1600-03-01 to 1970-01-01 */
    private static int DATE_1600_03_01 = 3*DAYS_STANDARD_CENTURY + (70/4)*DAYS_JULIAN_CYCLE + (70%4)*DAYS_STANDARD_YEAR
     - (31+28);

    private static final String[] SMALL_NUMBERS;

    /**
     * The number of seconds from the epoch of 1970-01-01T00:00:00Z.
     */
    private final long epochSeconds;
    /**
     * The number of nanoseconds, later along the time-line, from the seconds field.
     * This is always positive, and never exceeds 999,999,999.
     */
    private final int nanoOfSecond;

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

    protected AbstractInstant(long epochSeconds, int nanoOfSecond) {
        this.epochSeconds = epochSeconds;
        this.nanoOfSecond = nanoOfSecond;
    }

    /** TimeScale for this instant.
     *
     * @return associated time scale.
     */
    public abstract TimeScale getScale();

    /**
     * Gets the number of seconds from the epoch of 1970-01-01T00:00:00Z.
     * <p>
     * Instants on the time-line after the epoch are positive, earlier are negative.
     *
     * @return the seconds from the epoch
     */
    public long getEpochSeconds() {
        return epochSeconds;
    }

    /**
     * Gets the number of nanoseconds, later along the time-line, from the start
     * of the second returned by {@link #getEpochSeconds()}.
     *
     * @return the nanoseconds within the second, always positive, never exceeds 999,999,999
     */
    public int getNanoOfSecond() {
        return nanoOfSecond;
    }

    /** Seconds since 1970-01-01 without leap seconds.
     * @return seconds since the epoch
     */
    public long getSimpleEpochSeconds() {
        return getScale().getSimpleEpochSeconds(this);
    }

    /** leap second after simple epoch instant.
     * This is never negative. It is used to disambiguate seconds which share the same simple epoch seconds value.
     * @return 1 during a positive leap second.
     */
    public int getLeapSecond() {
        return 0;
    }

    protected abstract T factory(long epochSeconds, int nanoOfSecond, int leapSecond);
    protected T factory(long epochSeconds, int nanoOfSecond, int leapSecond, int includedLeapSeconds) {
        return factory(epochSeconds, nanoOfSecond, leapSecond);
    }

    /** Calculate duration from another instant.
     * To compute durations between instants which may be on different time scales, use the TimeScale.durationBetween
     * method. This is required because the result can depend on the TimeScale used to evaluate the difference. For
     * example the difference between 2008-12-31T23:59:59Z and 2009-01-01T:00:00Z is two seconds in most time scales
     * (and in reality), but only one second on time scales which ignore the leap second.
     * @param other another instant on the same time scale.
     * @return duration from other to this measured on the common time scale.
     */
    public Duration durationFrom(AbstractInstant other) {
        if (getScale().equals(other.getScale())) {
            return getScale().difference(this, other);
        }
        throw new IllegalArgumentException("Other duration on different time scale");
    }

    /**
     * Returns a copy of this Instant with the specified duration added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param duration  the duration to add, not null
     * @return a new updated Instant, never null
     * @throws ArithmeticException if the result exceeds the storage capacity
     */
    public T plus(Duration duration) {
        // Should the casts really be necessary here?
        // javac and IntelliJ disagree.
        return (T)getScale().plus((T)this, duration);
    }

    /**
     * Returns a copy of this Instant with the specified number of seconds added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param seconds  the seconds to add
     * @return a new updated Instant, never null
     * @throws ArithmeticException if the result exceeds the storage capacity
     */
    public T plusSeconds(long seconds) {
        return plus(Duration.duration(seconds));
    }

    /**
     * Returns a copy of this Instant with the specified number of milliseconds added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param millis  the milliseconds to add
     * @return a new updated Instant, never null
     * @throws ArithmeticException if the result exceeds the storage capacity
     */
    public T plusMillis(long millis) {
        return plus(Duration.millisDuration(millis));
    }

    /**
     * Returns a copy of this Instant with the specified number of nanoseconds added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param nanos  the nanoseconds to add
     * @return a new updated Instant, never null
     * @throws ArithmeticException if the result exceeds the storage capacity
     */
    public T plusNanos(long nanos) {
        long seconds = nanos/NANOS_PER_SECOND;
        int n = (int)(nanos%NANOS_PER_SECOND);
        if (n < 0) {
            n += NANOS_PER_SECOND;
            seconds--;
        }
        return plus(Duration.duration(seconds, n));
    }

    /**
     * Returns a copy of this Instant with the specified duration subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param duration  the duration to subtract, not null
     * @return a new updated Instant, never null
     * @throws ArithmeticException if the result exceeds the storage capacity
     */
    public T minus(Duration duration) {
        // Should the casts really be necessary here?
        // javac and IntelliJ disagree.
        return (T)getScale().minus((T)this, duration);
    }

    /**
     * Returns a copy of this Instant with the specified number of seconds subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param seconds  the seconds to subtract
     * @return a new updated Instant, never null
     * @throws ArithmeticException if the result exceeds the storage capacity
     */
    public T minusSeconds(long seconds) {
        return minus(Duration.duration(seconds));
    }

    /**
     * Returns a copy of this Instant with the specified number of milliseconds subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param millis  the milliseconds to subtract
     * @return a new updated Instant, never null
     * @throws ArithmeticException if the result exceeds the storage capacity
     */
    public T minusMillis(long millis) {
        return minus(Duration.millisDuration(millis));
    }

    /**
     * Returns a copy of this Instant with the specified number of nanoseconds subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param nanos  the nanoseconds to subtract
     * @return a new updated Instant, never null
     * @throws ArithmeticException if the result exceeds the storage capacity
     */
    public T minusNanos(long nanos) {
        long seconds = nanos/NANOS_PER_SECOND;
        int n = (int)(nanos%NANOS_PER_SECOND);
        if (n < 0) {
            n += NANOS_PER_SECOND;
            seconds--;
        }
        return minus(Duration.duration(seconds, n));
    }

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
