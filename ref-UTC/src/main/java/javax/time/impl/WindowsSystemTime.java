package javax.time.impl;

import javax.time.TimeSource;
import javax.time.scale.TimeScale;
import javax.time.scale.AbstractInstant;
import javax.time.scale.UTC_NoLeaps;

/** TimeSource based on Windows System Time 
 */
public class WindowsSystemTime extends TimeSource {
    public static final WindowsSystemTime SOURCE = new WindowsSystemTime();

    private static final int NANOSECONDS_PER_SECOND = 1000000000;
    private static final int FILETIME_PER_SECOND = 10000000;
    private static final int NANOS_PER_FILETIME = NANOSECONDS_PER_SECOND/FILETIME_PER_SECOND;
    private static final long MAX_VALUE_SECONDS = Long.MAX_VALUE/FILETIME_PER_SECOND;
    private static final int MAX_VALUE_NANOS = 1+(int)(Long.MAX_VALUE%FILETIME_PER_SECOND);

    private static int SECONDS_PER_DAY = 86400;
    private static int SECONDS_PER_YEAR = 365*SECONDS_PER_DAY;
    private static int SECONDS_PER_4YEAR = 4*SECONDS_PER_YEAR+SECONDS_PER_DAY;
    private static long SECONDS_PER_CENTURY = 25L*SECONDS_PER_4YEAR - SECONDS_PER_DAY;
    private static final long SECONDS_EPOCH_FROM_FILETIME = 3*SECONDS_PER_CENTURY + 17*SECONDS_PER_4YEAR + SECONDS_PER_YEAR;

    /** GetSystemTimeAsFileTime.
     * On Vista the returned value is a multiple of 10000 (i.e. in round milliseconds), whereas on XP the full
     * internal clock is returned.
     * @return time since 1601-01-01T00:00 in 100ns units. Should be interpreted as an <b>unsigned</b> long
     */
    static native long get();
    static native long getAdjustment();

    static UTC_NoLeaps.Instant instantFromFileTime(long filetime) {
        long seconds;
        int nanoOfSecond;
        if (filetime >= 0) {
            seconds = filetime/FILETIME_PER_SECOND;
            nanoOfSecond = (int)(filetime%FILETIME_PER_SECOND);
        }
        else {
            filetime = filetime - Long.MIN_VALUE;
            seconds = MAX_VALUE_SECONDS + filetime/FILETIME_PER_SECOND;
            nanoOfSecond = MAX_VALUE_NANOS+(int)(filetime%FILETIME_PER_SECOND) ;
            if (nanoOfSecond >= FILETIME_PER_SECOND) {
                nanoOfSecond -= FILETIME_PER_SECOND;
                seconds++;
            }
        }
        nanoOfSecond *= NANOS_PER_FILETIME;
        return UTC_NoLeaps.SCALE.instant(seconds-SECONDS_EPOCH_FROM_FILETIME, nanoOfSecond);
    }
    
    static {
        System.loadLibrary("lib/win32/jsr-310");
    }

    public TimeScale<?> getScale() {
        return UTC_NoLeaps.SCALE;
    }

    public UTC_NoLeaps.Instant instant() {
        return instantFromFileTime(get());
    }
}
