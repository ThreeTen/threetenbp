package javax.time.scale;

import java.io.Serializable;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.util.List;

/** A period in UTC history
*/
public abstract class UTCHistoryEntry implements Serializable {
    private static int MJD19700101 = Scale.modifiedJulianDay(1970, 1, 1);
    private static long SECONDS_PER_DAY = 86400L;
    /**
     * Constant for nanos per second.
     */
    private static final int NANOS_PER_SECOND = 1000000000;


    private transient UTCHistoryEntry previous;
    private transient UTCHistoryEntry next;
    private UTC.Instant startUTC;
    private transient TAI.Instant startTAI;
    private transient int leapSecondCount;

    static void setNextPrevious(List<UTCHistoryEntry> list) {
        if (list.isEmpty())
            return;
        int leapSecondCount = 0;
        UTCHistoryEntry previous = null;
        for (UTCHistoryEntry e: list) {
            if (previous != null)
                previous.next = e;
            e.previous = previous;
            previous = e;
            if (e.isLeapEntry())
                leapSecondCount++;
            e.leapSecondCount = leapSecondCount;
        }
        if (previous != null)
            previous.next = null;
    }

    /** Simple parsing of a plain date */
    static UTC.Instant parseInstant(String s) {
        if (s == null || s.length() == 0)
            return null;
        int x = s.indexOf('-');
        int year = Integer.parseInt(s.substring(0, x));
        x++;
        int y = s.indexOf('-', x);
        int month = Integer.parseInt(s.substring(x, y), 10);    // force radix 10 even if leading zeros
        int day = Integer.parseInt(s.substring(y+1), 10);
        long epochSeconds = (Scale.modifiedJulianDay(year, month, day) - MJD19700101) * SECONDS_PER_DAY;
        return new UTC.Instant(epochSeconds, 0);
    }

    static UTCHistoryEntry parseEntry(String[] fields) {
        UTC.Instant startUTC = parseInstant(fields[1]);
        long delta0 = Math.round(NANOS_PER_SECOND*Double.parseDouble(fields[2]));    // offset in nanoseconds
        UTCHistoryEntry entry;
        if (fields[0].equals("standard")) {
            entry = new StandardSecond(startUTC, delta0);
        }
        else if (fields[0].equals("leap")) {
            entry = new LeapEntry(startUTC, delta0);
        }
        else if (fields[0].equals("adjust")) {
            entry = new AdjustedSecond(startUTC, delta0, fields);
        }
        else
            throw new IllegalArgumentException("Unexpected UTCHistoryEntry type: "+fields[0]);
        entry.computeStartTAI();
        return entry;
    }

    protected UTCHistoryEntry(UTC.Instant startUTC) {
        this.startUTC = startUTC;
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        computeStartTAI();
    }

    private void computeStartTAI() {
        if (startUTC == null)
            return;
        long epochSeconds = startUTC.getEpochSeconds();
        int nanoOfSecond = startUTC.getNanoOfSecond();
        long nanos = nanoOfSecond+getUTCDeltaNanoseconds(epochSeconds, nanoOfSecond);
        epochSeconds += nanos/NANOS_PER_SECOND;
        nanoOfSecond = (int)(nanos%NANOS_PER_SECOND);
        if (nanoOfSecond < 0) {
            nanoOfSecond += NANOS_PER_SECOND;
            epochSeconds--;
        }
        startTAI = new TAI.Instant(epochSeconds, nanoOfSecond);
    }

    /** preceding entry if any.
     * @return preceding entry or null if this is the first entry.
     */
    public UTCHistoryEntry getPrevious() {
        return previous;
    }

    /** next entry if any.
     * @return next entry or null if this is the last entry.
     */
    public UTCHistoryEntry getNext() {
        return next;
    }

    /** Start of period covered by entry.
     * Period starts are always specified in UTC. They are also never leap seconds, so
     * we can use any UTC Instant unambiguously.
     * @return null for beginning of time, otherwise Instant representing the start.
     */
    public UTC.Instant getStartUTC() {
        return startUTC;
    }

    public TAI.Instant getStartTAI() {
        return startTAI;
    }

    /** Is this period immediately preceded by a leap second?
     * @return true if period immediately follows a leap second
     */
    public boolean isLeapEntry() {
        return false;
    }

    /** Number of leap seconds prior to this period.
     * This is the count of leap seconds since their introduction in 1972.
     * @return number of leap seconds inserted before this period.
     */
    public int getLeapSecondCount() {
        return leapSecondCount;
    }

    /** Does the period use standard SI seconds?
     *
     * @return true for periods using SI seconds, false otherwise.
     */
    public abstract boolean isStandardSecond();

    /** Compute TAI-UTC in nanoseconds given UTC time instant.
     *
     * @param utcEpochSeconds
     * @param nanoOfSecond
     * @return TAI-UTC in nanoseconds
     */
    public abstract long getUTCDeltaNanoseconds(long utcEpochSeconds, int nanoOfSecond);

    /** Compute TAI-UTC in nanoseconds given TAI instant.
     * @param taiEpochSeconds
     * @param nanoOfSecond
     * @return TAI-UTC in nanoseconds
     */
    public abstract long getTAIDeltaNanoseconds(long taiEpochSeconds, int nanoOfSecond);

    private static class AdjustedSecond extends UTCHistoryEntry {
        private final long delta0;
        private final int mjd0;
        private final int rateNanoseconds;

        private transient long utcOrigin;  // origin in epoch seconds
        private transient double utcRate; // multiplier delta nanoseconds/second
        private transient double taiRate;

        AdjustedSecond(UTC.Instant startUTC, long delta0, String[] fields) {
            super(startUTC);
            this.delta0 = delta0;
            mjd0 = Integer.parseInt(fields[3]);
            rateNanoseconds = (int)Math.round(NANOS_PER_SECOND*Double.parseDouble(fields[4]));
            computeTerms();
        }

        private void computeTerms() {
            utcOrigin = SECONDS_PER_DAY * (mjd0-MJD19700101);    // convert origin from MJD to epochSeconds
            utcRate = rateNanoseconds/(double)SECONDS_PER_DAY;  // convert rate to nanoseconds per second
            taiRate = utcRate/(1+utcRate/NANOS_PER_SECOND);
        }

        private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
            stream.defaultReadObject();
            computeTerms();
        }

        public long getUTCDeltaNanoseconds(long utcEpochSeconds, int nanoOfSecond) {
            return delta0+Math.round(utcRate*((utcEpochSeconds-utcOrigin)+1e-9*nanoOfSecond));
        }

        public long getTAIDeltaNanoseconds(long taiEpochSeconds, int nanoOfSecond) {
            return delta0+Math.round(taiRate*((taiEpochSeconds-utcOrigin)+1e-9*(nanoOfSecond-delta0)));
        }

        @Override
        public String toString() {
            StringBuilder buffer = new StringBuilder();
            buffer.append(getStartUTC()).append(": ").append(delta0/(double)NANOS_PER_SECOND).append(" + (MJD-")
                    .append(mjd0).append(")*").append(rateNanoseconds/(double)NANOS_PER_SECOND);
            return buffer.toString();
        }

        public boolean isStandardSecond() {
            return false;
        }
    }

    /** An entry for a period using standard SI seconds.
     *
     */
    private static class StandardSecond extends UTCHistoryEntry {
        private final long deltaNanoseconds;

        StandardSecond(UTC.Instant startUTC, long deltaNanoseconds) {
            super(startUTC);
            this.deltaNanoseconds = deltaNanoseconds;
        }

        @Override
        public String toString() {
            StringBuilder buffer = new StringBuilder();
            buffer.append(getStartUTC()).append(": ").append(deltaNanoseconds/(double)NANOS_PER_SECOND);
            return buffer.toString();    //To change body of overridden methods use File | Settings | File Templates.
        }

        public boolean isStandardSecond() {
            return true;
        }

        public long getUTCDeltaNanoseconds(long utcEpochSeconds, int nanoOfSecond) {
            return deltaNanoseconds;
        }

        public long getTAIDeltaNanoseconds(long taiEpochSeconds, int nanoOfSecond) {
            return deltaNanoseconds;
        }
    }

    /** Start of entry is immediately preceded by a leap second.
     */
    private static class LeapEntry extends StandardSecond {
        LeapEntry(UTC.Instant startUTC, long deltaNanoseconds) {
            super(startUTC, deltaNanoseconds);
        }

        @Override
        public String toString() {
            return "Leap;"+super.toString()+"; leapSecondCount="+getLeapSecondCount();
        }

        @Override
        public boolean isLeapEntry() {
            return true;
        }
    }
}
