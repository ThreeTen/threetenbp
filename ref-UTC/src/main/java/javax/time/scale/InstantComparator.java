package javax.time.scale;

import javax.time.Instant;
import java.util.Comparator;

/** Compare AbstractInstant's
 */
class InstantComparator implements Comparator<Instant> {
    public static final InstantComparator INSTANCE = new InstantComparator();

    public int compare(Instant a, Instant b) {
        if (a.getEpochSeconds() != b.getEpochSeconds()) {
            return a.getEpochSeconds() < b.getEpochSeconds() ? -1 : 1;
        }
        if (a.getLeapSecond() != b.getLeapSecond()) {
            return a.getLeapSecond() < b.getLeapSecond() ? -1 : 1;
        }
        if (a.getNanoOfSecond() == b.getNanoOfSecond())
            return 0;
        return a.getNanoOfSecond() < b.getNanoOfSecond() ? -1 : 1;
    }
}
