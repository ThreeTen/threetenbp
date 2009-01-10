package javax.time.scale;

import javax.time.Instant;

/** Helper methods for TimeScale tests.
 * These may duplicate some functionality found elsewhere.
 */
class TestScale {
    static int MJD19700101 = Scale.modifiedJulianDay(1970, 1, 1);
    static long SECONDS_PER_DAY = 86400L;

    /** seconds since 1970-01-01 ignoring leap seconds. */
    static long date(int year, int month, int day) {
        return (Scale.modifiedJulianDay(year, month, day) - MJD19700101) * SECONDS_PER_DAY;
    }

    /** seconds since midnight */
    static int time(int hours, int minutes, int seconds) {
        return (hours*60+minutes)*60+seconds;
    }

    public static void main(String[] args) {
        System.out.println(Instant.instant(Long.MAX_VALUE));
        System.out.println(TimeScaleInstant.instant(TAI.SCALE, Long.MIN_VALUE, 0));
        System.out.println(TimeScaleInstant.instant(TAI.SCALE, Long.MAX_VALUE, 0));
        System.out.println(TimeScaleInstant.leapInstant(UTC_NoEpochLeaps.SCALE, date(2008, 12, 31) + time(23, 59, 59), 500000000, 1).toString());
        System.out.println(TimeScaleInstant.instant(UTC_NoEpochLeaps.SCALE, date(1600, 1, 1), 0));
        System.out.println(TimeScaleInstant.instant(UTC_NoEpochLeaps.SCALE, date(1599, 11, 7), 0));
        System.out.println(TimeScaleInstant.instant(UTC_NoEpochLeaps.SCALE, date(1600, 2, 28), 0));
        System.out.println(TimeScaleInstant.instant(UTC_NoEpochLeaps.SCALE, date(2008, 2, 28), 0));
        System.out.println(TimeScaleInstant.instant(UTC_NoEpochLeaps.SCALE, date(1600, 2, 29), 0));
        System.out.println(TimeScaleInstant.instant(UTC_NoEpochLeaps.SCALE, date(2008, 2, 29), 0));
        System.out.println(TimeScaleInstant.instant(UTC_NoEpochLeaps.SCALE, date(2000, 2, 29), 0));
        System.out.println(TimeScaleInstant.instant(UTC_NoEpochLeaps.SCALE, date(970, 2, 29), 0));
        AbstractInstant t = TimeScaleInstant.instant(UTC_NoEpochLeaps.SCALE, date(-1,2,2), 0);
        System.out.println(t.toString());
        System.out.println(Instant.instant(date(-1, 2, 2)));
    }

    private TestScale() {}
}
