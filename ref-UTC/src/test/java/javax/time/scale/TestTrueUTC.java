package javax.time.scale;

import org.testng.annotations.Test;
import static org.testng.Assert.*;
import static javax.time.scale.TestScale.*;
import javax.time.TimeScale;
import javax.time.Instant;
import javax.time.Duration;

/** Test conversions between UTC and TAI.
 * Expected values are taken from external sources, computed by hand, or
 * computed by different code.
 */
@Test
public class TestTrueUTC {
    private static final int NANOS_PER_SECOND = 1000000000;

    private void testScaleConversion(TimeScale src, long epochSeconds, TimeScale dst, long deltaEpochSeconds, int nanoOfSecond) {
        testScaleConversion(src, epochSeconds, dst, deltaEpochSeconds, nanoOfSecond, 0);
    }

    private void testScaleConversion(TimeScale src, long epochSeconds, TimeScale dst, long deltaEpochSeconds, int nanoOfSecond, int leapSecond) {
        Instant tSrc = src.instant(epochSeconds);
        Instant tDst = dst.instant(tSrc);
        // testNG is actual, expected (reverse of JUnit)
        assertEquals(tDst.getEpochSeconds(), epochSeconds+deltaEpochSeconds);
        assertEquals(tDst.getNanoOfSecond(), nanoOfSecond);
        assertEquals(tDst.getLeapSecond(), leapSecond);
        assertEquals(src.instant(tDst), tSrc);  // check round trip
    }

    public void testConvertUTC() {
        testScaleConversion(TrueUTC.SCALE, date(1961, 1, 1), TAI.SCALE, 1, 422818000);
        testScaleConversion(TrueUTC.SCALE, date(1970, 1, 1), TAI.SCALE, 8, 82000);
        testScaleConversion(TrueUTC.SCALE, date(1972, 1, 1)-1, TAI.SCALE, 9, 892241970);    // last irregular jump 0.107758 TAI, + 30ns for the second
        testScaleConversion(TrueUTC.SCALE, date(1972, 1, 1), TAI.SCALE, 10, 0);
        testScaleConversion(TrueUTC.SCALE, date(1972, 7, 1)-1, TAI.SCALE, 10, 0);
        testScaleConversion(TrueUTC.SCALE, date(1972, 7, 1), TAI.SCALE, 11, 0);
        testScaleConversion(TrueUTC.SCALE, date(2009, 1, 1)-1, TAI.SCALE, 33, 0);
        testScaleConversion(TrueUTC.SCALE, date(2009, 1, 1), TAI.SCALE, 34, 0);
        testScaleConversion(TAI.SCALE, date(1970, 1, 1), TrueUTC.SCALE, -9, 999918240); // (-8.000081760)
    }

    private void testHistory(TimeScale scale, int year, int month, int day, double baseDelta, int baseMJD, double rate)
    {
        // Compute expected value of TAI-UTC
        double expectedDelta = baseDelta+rate*(Scale.modifiedJulianDay(year, month, day)-baseMJD);
        long delta = Math.round(expectedDelta*NANOS_PER_SECOND);
        int deltaSeconds = (int)(delta/NANOS_PER_SECOND);
        int deltaNanoseconds = (int)(delta%NANOS_PER_SECOND);
        long epochSeconds = date(year, month, day);
        Instant t = scale.instant(epochSeconds);
        Instant tai = TAI.SCALE.instant(t);
        assertEquals(tai.getEpochSeconds()-epochSeconds, deltaSeconds);
        assertEquals(tai.getNanoOfSecond(), deltaNanoseconds);
    }

    private void testHistoryUTC(TimeScale scale)
    {
        testHistory(scale, 1961, 1, 1, 1.422818, 37300, 0.001296);  // Formal beginning of UTC
        testHistory(scale, 1961, 7, 31, 1.422818, 37300, 0.001296);
        testHistory(scale, 1961, 8, 1, 1.372818, 37300, 0.001296); // -0.05s
        testHistory(scale, 1962, 1, 1, 1.845858, 37665, 0.0011232); // 0s
        testHistory(scale, 1963, 11, 1, 1.945858, 37665, 0.0011232); // +0.1
        testHistory(scale, 1964, 1, 1, 3.240130, 38761, 0.001296); // 0s
        testHistory(scale, 1964, 4, 1, 3.340130, 38761, 0.001296); // +0.1s
        testHistory(scale, 1964, 9, 1, 3.440130, 38761, 0.001296); // +0.1s
        testHistory(scale, 1965, 1, 1, 3.540130, 38761, 0.001296); // +0.1s
        testHistory(scale, 1965, 3, 1, 3.640130, 38761, 0.001296); // +0.1s
        testHistory(scale, 1965, 7, 1, 3.740130, 38761, 0.001296); // +0.1s
        testHistory(scale, 1965, 9, 1, 3.840130, 38761, 0.001296); // +0.1s
        testHistory(scale, 1966, 1, 1, 4.313170, 39126, 0.002592); // 0s
        testHistory(scale, 1968, 2, 1, 4.213170, 39126, 0.002592); // -0.1s
    }

    public void testHistory()
    {
        testHistoryUTC(TrueUTC.SCALE);
        testHistoryUTC(UTC_NoLeaps.SCALE);
    }

    private void testDiscontinuity(TimeScale scale, int year, int month, int day, long stepNanoseconds)
    {
        long epochSeconds = date(year, month, day);
        Instant t1 = scale.instant(epochSeconds-1, NANOS_PER_SECOND-1);
        Instant t2 = scale.instant(epochSeconds);
        Duration step = TAI.SCALE.durationBetween(t2, t1);
        long expectedSeconds;
        int expectedNanoOfSecond;
        expectedSeconds = stepNanoseconds/NANOS_PER_SECOND;
        expectedNanoOfSecond = (int)(stepNanoseconds%NANOS_PER_SECOND);
        if (expectedNanoOfSecond < 0) {
            expectedSeconds--;
            expectedNanoOfSecond += NANOS_PER_SECOND;
        }
        assertEquals(step.getSeconds(), expectedSeconds);
        assertEquals(step.getNanoOfSecond()-1, expectedNanoOfSecond);
    }

    private void testOldDiscontinuities(TimeScale scale)
    {
        // Note negative steps imply a 'gap' in the UTC scale, while positive values
        // give ambiguous times (i.e. there are two TAI instants associated with a single UTC value).
        testDiscontinuity(scale, 1961, 8, 1, -50000000);
        testDiscontinuity(scale, 1962, 1, 1, 0);
        testDiscontinuity(scale, 1963, 11, 1, 100000000);
        testDiscontinuity(scale, 1964, 1, 1, 0);
        testDiscontinuity(scale, 1964, 4, 1, 100000000);
        testDiscontinuity(scale, 1964, 9, 1, 100000000);
        testDiscontinuity(scale, 1965, 1, 1, 100000000);
        testDiscontinuity(scale, 1965, 3, 1, 100000000);
        testDiscontinuity(scale, 1965, 7, 1, 100000000);
        testDiscontinuity(scale, 1965, 9, 1, 100000000);
        testDiscontinuity(scale, 1966, 1, 1, 0);
        testDiscontinuity(scale, 1968, 2, 1, -100000000);
        testDiscontinuity(scale, 1972, 1, 1, 107758000);
    }

    public void testOldDiscontinuities()
    {
        testOldDiscontinuities(TrueUTC.SCALE);
        testOldDiscontinuities(UTC_NoLeaps.SCALE);
    }

    public void testLeapSeconds() {
        testDiscontinuity(TrueUTC.SCALE, 1972, 7, 1, NANOS_PER_SECOND);
        testDiscontinuity(TrueUTC.SCALE, 1973, 1, 1, NANOS_PER_SECOND);
        testDiscontinuity(TrueUTC.SCALE, 1974, 1, 1, NANOS_PER_SECOND);
        testDiscontinuity(TrueUTC.SCALE, 1975, 1, 1, NANOS_PER_SECOND);
        testDiscontinuity(TrueUTC.SCALE, 1976, 1, 1, NANOS_PER_SECOND);
        testDiscontinuity(TrueUTC.SCALE, 1977, 1, 1, NANOS_PER_SECOND);
        testDiscontinuity(TrueUTC.SCALE, 1978, 1, 1, NANOS_PER_SECOND);
        testDiscontinuity(TrueUTC.SCALE, 1979, 1, 1, NANOS_PER_SECOND);
        testDiscontinuity(TrueUTC.SCALE, 1980, 1, 1, NANOS_PER_SECOND);
        testDiscontinuity(TrueUTC.SCALE, 1981, 7, 1, NANOS_PER_SECOND);
        testDiscontinuity(TrueUTC.SCALE, 1982, 7, 1, NANOS_PER_SECOND);
        testDiscontinuity(TrueUTC.SCALE, 1983, 7, 1, NANOS_PER_SECOND);
        testDiscontinuity(TrueUTC.SCALE, 1985, 7, 1, NANOS_PER_SECOND);
        testDiscontinuity(TrueUTC.SCALE, 1988, 1, 1, NANOS_PER_SECOND);
        testDiscontinuity(TrueUTC.SCALE, 1990, 1, 1, NANOS_PER_SECOND);
        testDiscontinuity(TrueUTC.SCALE, 1991, 1, 1, NANOS_PER_SECOND);
        testDiscontinuity(TrueUTC.SCALE, 1992, 7, 1, NANOS_PER_SECOND);
        testDiscontinuity(TrueUTC.SCALE, 1993, 7, 1, NANOS_PER_SECOND);
        testDiscontinuity(TrueUTC.SCALE, 1994, 7, 1, NANOS_PER_SECOND);
        testDiscontinuity(TrueUTC.SCALE, 1996, 1, 1, NANOS_PER_SECOND);
        testDiscontinuity(TrueUTC.SCALE, 1997, 7, 1, NANOS_PER_SECOND);
        testDiscontinuity(TrueUTC.SCALE, 1999, 1, 1, NANOS_PER_SECOND);
        testDiscontinuity(TrueUTC.SCALE, 2006, 1, 1, NANOS_PER_SECOND);
        testDiscontinuity(TrueUTC.SCALE, 2009, 1, 1, NANOS_PER_SECOND);
    }

    public void testArithmetic() {
        TimeScale scale = TrueUTC.SCALE;
        long epochSeconds = date(2009, 1, 1);  // immediately after a leap second
        Instant a = scale.instant(epochSeconds);
        assertEquals(a.getEpochSeconds(), epochSeconds);
        Instant b = scale.instant(epochSeconds-1);
        assertEquals(b.getEpochSeconds(), epochSeconds-1);
        Duration diff = a.durationFrom(b);
        assertEquals(diff.getSeconds(), 2, "leap second");
        assertEquals(diff.getNanoOfSecond(), 0);
        Instant c = scale.instant(epochSeconds-2);
        diff = b.durationFrom(c);
        assertEquals(diff.getSeconds(), 1);
        assertEquals(diff.getNanoOfSecond(), 0);
        Instant d = b.plusSeconds(1);
        assertEquals(d.getEpochSeconds(), b.getEpochSeconds());
        assertEquals(d.getNanoOfSecond(), 0);
        assertEquals(d.getLeapSecond(), 1);
        Instant e = a.minusSeconds(1);
        assertEquals(e, d);
        Instant f = scale.instant(epochSeconds-1, 0, 1);
        assertEquals(f, e);
        assertEquals(e, f);
    }

}
