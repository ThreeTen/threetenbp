package javax.time.scale;

import javax.time.TimeScale;

/** Point in time relative to a time scale.
 * Time conversions can be done simply by subtracting the included leap seconds from
 * the epochSeconds and the later (optionally) adjusting for leapSecond if that is non zero. 
 * @author Mark Thornton
 */
public class TimeScaleInstant extends AbstractInstant {
    private final TimeScale scale;
    private final long epochSeconds;
    private final int nanoOfSecond;

    public static TimeScaleInstant instant(TimeScale scale, long epochSeconds, int nanoOfSecond) {
        return new TimeScaleInstant(scale, epochSeconds, nanoOfSecond);
    }

    public static TimeScaleInstant instantWithIncludedLeaps(TimeScale scale, long epochSeconds, int nanoOfSecond, int includedLeapSeconds) {
        return new WithIncludedLeaps(scale, epochSeconds, nanoOfSecond, includedLeapSeconds);
    }

    public static TimeScaleInstant leapInstant(TimeScale scale, long epochSeconds, int nanoOfSecond, int leapSecond) {
        if (leapSecond == 0)
            return new TimeScaleInstant(scale, epochSeconds, nanoOfSecond);
        else
            return new LeapInstant(scale, epochSeconds, nanoOfSecond, leapSecond);
    }

    public static TimeScaleInstant leapInstantWithIncludedLeaps(TimeScale scale, long epochSeconds, int nanoOfSecond, int includedLeapSeconds, int leapSecond) {
        if (leapSecond == 0)
            return new WithIncludedLeaps(scale, epochSeconds, nanoOfSecond, includedLeapSeconds);
        else
            return new LeapInstantWithIncludedLeaps(scale, epochSeconds, nanoOfSecond, includedLeapSeconds, leapSecond);
    }

    private TimeScaleInstant(TimeScale scale, long epochSeconds, int nanoOfSecond) {
        if (scale == null)
            throw new NullPointerException("Must specify time scale");
        this.scale = scale;
        this.epochSeconds = epochSeconds;
        this.nanoOfSecond = nanoOfSecond;
    }

    public TimeScale getScale() {return scale;}

    /** Seconds since 1 January 1970.
     *
     * @return seconds measured on a time scale
     */
    public long getEpochSeconds() { return epochSeconds;}
    public int getNanoOfSecond() {return nanoOfSecond;}

    /** Seconds since 1 January 1970, ignoring leap seconds.
     * @return epochSeconds without any included leap seconds 
     */
    public long getSimpleEpochSeconds() {return epochSeconds;}

    /** Does epochSeconds include any leap seconds? */
    public boolean isLeapSecondTotalIncluded() {return false;}

    /** Number of leap seconds included in epochSeconds.
     * Zero for timescales which do not have leap seconds or where the leap seconds
     * are ignored.
     * @return total leap seconds included.
     */
    public int getIncludedLeapSeconds() {return 0;}

    /** Second within a leap.
     * Zero for non leap seconds. Plus one for all leap seconds which have occurred up to 2009.
     * Could be +2 or greater in future.
     * @return
     */
    public int getLeapSecond() {return 0;}

    private static class LeapInstant extends TimeScaleInstant {
        private final int leapSecond;

        private LeapInstant(TimeScale scale, long epochSeconds, int nanoOfSecond, int leapSecond) {
            super(scale, epochSeconds, nanoOfSecond);
            this.leapSecond = leapSecond;
        }

        @Override
        public int getLeapSecond() {
            return leapSecond;
        }
    }

    private static class WithIncludedLeaps extends TimeScaleInstant {
        private final int includedLeaps;

        private WithIncludedLeaps(TimeScale scale, long epochSeconds, int nanoOfSecond, int includedLeaps) {
            super(scale, epochSeconds, nanoOfSecond);
            this.includedLeaps = includedLeaps;
        }

        @Override
        public boolean isLeapSecondTotalIncluded() {
            return true;
        }

        @Override
        public long getSimpleEpochSeconds() {
            return getEpochSeconds()-includedLeaps-getLeapSecond();
        }

        @Override
        public int getIncludedLeapSeconds() {
            return includedLeaps;
        }
    }

    private static class LeapInstantWithIncludedLeaps extends WithIncludedLeaps {
        private final int leapSecond;

        LeapInstantWithIncludedLeaps(TimeScale scale, long epochSeconds, int nanoOfSecond, int includedLeaps, int leapSecond) {
            super(scale, epochSeconds, nanoOfSecond, includedLeaps);
            this.leapSecond = leapSecond;
        }

        @Override
        public int getLeapSecond() {
            return leapSecond;
        }
    }
}
