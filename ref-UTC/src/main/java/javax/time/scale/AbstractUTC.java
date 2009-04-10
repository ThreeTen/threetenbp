package javax.time.scale;

import javax.time.TimeScale;
import javax.time.MathUtils;
import javax.time.Instant;
import java.util.List;
import java.util.Collections;
import java.util.Arrays;

/** Model UTC before 1972.
 * Prior to the start of UTC in 1961-01-01 time reverts to TAI. It might be better to use a smooth transition ending at 
 * parity in 1958-01-01 (when TAI matched UT2).
 * @author Mark Thornton
 */
public abstract class AbstractUTC extends TimeScale {
    protected abstract Instant newInstant(long epochSeconds, int nanoOfSecond);

    protected Instant fromTAI(TAI.Instant tsiTAI) {
        // if before 1961 simply return input
        UTCHistoryEntry entry = UTCHistory.current().findEntry(tsiTAI);
        long s = tsiTAI.getEpochSeconds();
        int nanos = tsiTAI.getNanoOfSecond();
        long delta = entry.getTAIDeltaNanoseconds(s, nanos) - nanos;
        assert delta > 0;   // true for 1961 - 1972
        nanos = (int)(delta%NANOS_PER_SECOND);
        s -= (delta-nanos)/NANOS_PER_SECOND;
        if (nanos > 0) {
            nanos = NANOS_PER_SECOND - nanos;
            s--;
        }
        return newInstant(s, nanos);
    }

    protected TAI.Instant toTAI(long epochSeconds, int nanoOfSecond) {
        assert epochSeconds < UTCHistory.UTC_START_LEAP_SECONDS.getEpochSeconds();
        UTCHistoryEntry entry = UTCHistory.current().findEntrySimple(epochSeconds);
        long delta = entry.getUTCDeltaNanoseconds(epochSeconds, nanoOfSecond);
        delta += nanoOfSecond;
        return new TAI.Instant(MathUtils.safeAdd(epochSeconds, delta/NANOS_PER_SECOND), (int)(delta % NANOS_PER_SECOND));
    }

    protected TAI.Instant toTAI(Instant tsi) {
        assert tsi.getLeapSecond() == 0;
        return toTAI(tsi.getEpochSeconds(), tsi.getNanoOfSecond());
    }
}
