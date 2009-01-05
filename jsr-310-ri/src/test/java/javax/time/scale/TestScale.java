package javax.time.scale;

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

    private TestScale() {}
}
