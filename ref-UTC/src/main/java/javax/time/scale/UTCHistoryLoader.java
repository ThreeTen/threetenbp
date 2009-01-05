package javax.time.scale;

import java.util.ArrayList;
/*
    From http://hpiers.obspm.fr/eoppc/bul/bulc/UTC-TAI.history
    A similar table can be found at http://maia.usno.navy.mil/ser7/tai-utc.dat
 ---------------
 UTC-TAI.history
 ---------------
 RELATIONSHIP BETWEEN TAI AND UTC, UNTIL 27 DECEMBER 2005
 -------------------------------------------------------------------------------
 Limits of validity(at 0h UTC)       TAI - UTC

 1961  Jan.  1 - 1961  Aug.  1     1.422 818 0s + (MJD - 37 300) x 0.001 296s
       Aug.  1 - 1962  Jan.  1     1.372 818 0s +        ""
 1962  Jan.  1 - 1963  Nov.  1     1.845 858 0s + (MJD - 37 665) x 0.001 123 2s
 1963  Nov.  1 - 1964  Jan.  1     1.945 858 0s +        ""
 1964  Jan.  1 -       April 1     3.240 130 0s + (MJD - 38 761) x 0.001 296s
       April 1 -       Sept. 1     3.340 130 0s +        ""
       Sept. 1 - 1965  Jan.  1     3.440 130 0s +        ""
 1965  Jan.  1 -       March 1     3.540 130 0s +        ""
       March 1 -       Jul.  1     3.640 130 0s +        ""
       Jul.  1 -       Sept. 1     3.740 130 0s +        ""
       Sept. 1 - 1966  Jan.  1     3.840 130 0s +        ""
 1966  Jan.  1 - 1968  Feb.  1     4.313 170 0s + (MJD - 39 126) x 0.002 592s
 1968  Feb.  1 - 1972  Jan.  1     4.213 170 0s +        ""
 1972  Jan.  1 -       Jul.  1    10s
       Jul.  1 - 1973  Jan.  1    11s
 1973  Jan.  1 - 1974  Jan.  1    12s
 1974  Jan.  1 - 1975  Jan.  1    13s
 1975  Jan.  1 - 1976  Jan.  1    14s
 1976  Jan.  1 - 1977  Jan.  1    15s
 1977  Jan.  1 - 1978  Jan.  1    16s
 1978  Jan.  1 - 1979  Jan.  1    17s
 1979  Jan.  1 - 1980  Jan.  1    18s
 1980  Jan.  1 - 1981  Jul.  1    19s
 1981  Jul.  1 - 1982  Jul.  1    20s
 1982  Jul.  1 - 1983  Jul.  1    21s
 1983  Jul.  1 - 1985  Jul.  1    22s
 1985  Jul.  1 - 1988  Jan.  1    23s
 1988  Jan.  1 - 1990  Jan.  1    24s
 1990  Jan.  1 - 1991  Jan.  1    25s
 1991  Jan.  1 - 1992  Jul.  1    26s
 1992  Jul.  1.- 1993  Jul   1    27s
 1993  Jul.  1 - 1994  Jul.  1    28s
 1994  Jul.  1 - 1996  Jan.  1    29s
 1996  Jan.  1 - 1997  Jul.  1    30s
 1997  Jul.  1.- 1999  Jan.  1    31s
 1999  Jan.  1.- 2006  Jan.  1    32s
 2006  Jan.  1.- 2009  Jan.  1    33s
 2009  Jan.  1.-                  34s

 */

/** Create entries.
 * @author Mark Thornton
 */
class UTCHistoryLoader {
    private static int MJD19700101 = Scale.modifiedJulianDay(1970, 1, 1);
    private static long SECONDS_PER_DAY = 86400L;

    private ArrayList<AbstractUTC.Entry> entries = new ArrayList<AbstractUTC.Entry>();

    public UTCHistoryLoader() {
        createBefore1961();
        create1961to1972();
        createAfter1972();
    }

    public AbstractUTC.Entry[] entries()
    {
        AbstractUTC.Entry[] result = new AbstractUTC.Entry[entries.size()];
        return entries.toArray(result);
    }

    private AbstractUTC.Entry entry() {
        AbstractUTC.Entry e = new AbstractUTC.Entry();
        if (!entries.isEmpty()) {
            e.startInclusive(entries.get(entries.size()-1).getEndExclusiveSeconds());
        }
        entries.add(e);
        return e;
    }

    private long date(int year, int month, int day) {
        return (Scale.modifiedJulianDay(year, month, day) - MJD19700101) * SECONDS_PER_DAY;
    }

    private void createBefore1961() {

    }

    private AbstractUTC.Entry delta(int year, int month, int day, long deltaNanos, int mjdOrigin, int rateNanos) {
        long t = date(year, month, day);
        AbstractUTC.Entry e;
        if (!entries.isEmpty()) {
            e = entries.get(entries.size()-1);
            e.endExclusive(t);
        }
        e = new AbstractUTC.Entry();
        e.startInclusive(t).delta(deltaNanos).rate(rateNanos, mjdOrigin);
        entries.add(e);
        return e;
    }

    private AbstractUTC.Entry delta(int year, int month, int day, long deltaNanos) {
        AbstractUTC.Entry previous = entries.get(entries.size()-1);
        return delta(year, month, day, deltaNanos, previous.getOriginMJD(), previous.getRateNanoseconds());
    }

    private void create1961to1972() {
        delta(1961, 1, 1, 1422818000L, 37300, 1296000);
        delta(1961, 8, 1, 1372818000L);
        delta(1962, 1, 1, 1845858000L, 37665, 1123200);
        delta(1963,11, 1, 1945858000L);
        delta(1964, 1, 1, 3240130000L, 38761, 1296000);
        delta(1964, 4, 1, 3340130000L);
        delta(1964, 9, 1, 3440130000L);
        delta(1965, 1, 1, 3540130000L);
        delta(1965, 3, 1, 3640130000L);
        delta(1965, 7, 1, 3740130000L);
        delta(1965, 9, 1, 3840130000L);
        delta(1966, 1, 1, 4313170000L, 39126, 2592000);
        delta(1968, 2, 1, 4213170000L).endExclusive(date(1972, 1, 1));
    }

    private AbstractUTC.Entry leapSecond(int year, int month, int day) {
        long t = date(year, month, day);
        AbstractUTC.Entry e = entries.get(entries.size()-1);
        e.endExclusive(t);
        int delta = e.getDeltaSeconds();
        e = new AbstractUTC.Entry();
        e.startInclusive(t).deltaSeconds(delta+1);
        entries.add(e);
        return e;
    }

    private void createAfter1972() {
        entry().startInclusive(date(1972,1,1)).deltaSeconds(10);
        leapSecond(1972, 7, 1);
        leapSecond(1973, 1, 1);
        leapSecond(1974, 1, 1);
        leapSecond(1975, 1, 1);
        leapSecond(1976, 1, 1);
        leapSecond(1977, 1, 1);
        leapSecond(1978, 1, 1);
        leapSecond(1979, 1, 1);
        leapSecond(1980, 1, 1);
        leapSecond(1981, 7, 1);
        leapSecond(1982, 7, 1);
        leapSecond(1983, 7, 1);
        leapSecond(1985, 7, 1);
        leapSecond(1988, 1, 1);
        leapSecond(1990, 1, 1);
        leapSecond(1991, 1, 1);
        leapSecond(1991, 1, 1);
        leapSecond(1992, 7, 1);
        leapSecond(1993, 7, 1);
        leapSecond(1994, 7, 1);
        leapSecond(1996, 1, 1);
        leapSecond(1997, 7, 1);
        leapSecond(2006, 1, 1);
        leapSecond(2009, 1, 1).endExclusive(Long.MAX_VALUE);
    }
}
