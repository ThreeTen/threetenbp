package javax.time.scale;

import javax.time.Duration;
import static javax.time.scale.TestScale.*;

/** Demonstrate Duration arithmetic.
 */
public class TestScaleArithmetic {

    public static void main(String[] args) {
        Duration step = Duration.millisDuration(1000);
        TimeScale[] scales = {UTC.SCALE, TAI.SCALE, UTC_NoLeaps.SCALE, UTC_NoEpochLeaps.SCALE};
        AbstractInstant a = UTC_NoLeaps.SCALE.instant(date(2008, 12, 31) + time(23, 59, 59), 0); // 2008-12-31T23:59:59Z
        AbstractInstant b = UTC_NoLeaps.SCALE.instant(date(1971, 12, 31) + time(23, 59, 58), 0); // 1971-12-31T23:59:58Z
        for (TimeScale ts: scales) {
            System.out.println("Scale="+ts.getName());
            showTime(ts, a, step);
            showTime(ts, b, step);
        }
    }

    private static void showTime(TimeScale scale, AbstractInstant start, Duration step) {
        AbstractInstant t = scale.instant(start);
        AbstractInstant tai0 = TAI.SCALE.instant(start);
        AbstractInstant t0 = t;
        System.out.println(t);
        for (int i=0; i<4; i++) {
            t = t.plus(step);
            System.out.println(t+", t-t0="+t.durationFrom(t0)+", trueDelta="+TAI.SCALE.durationBetween(t, tai0));
        }
        System.out.println();
    }
}
