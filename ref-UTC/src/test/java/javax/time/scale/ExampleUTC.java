package javax.time.scale;

import javax.time.Instant;
import javax.time.TimeScale;
import javax.time.Duration;
import static javax.time.scale.TestScale.*;

/** Simple demonstrations with UTC.
 */
public class ExampleUTC {
    public static void main(String[] args) {
        showDiscontinuities();
    }

    private static void convert(Instant src, TimeScale scale) {
        Instant dst = scale.instant(src);
        System.out.println(src+" ==> "+dst);
    }


    private static void showDiscontinuities() {
        // The UTC interval [1968-01-31T23:59:59.9, 1968-02-01T00:00) does not exist
        // nor did [1961-07-31T23:59:59.95, 1961-08-01T00:00) 
        Instant t = UTC.SCALE.instant(date(1968, 2, 1));
        convert(t.minus(Duration.millisDuration(100)), TAI.SCALE);
        convert(t.minus(Duration.millisDuration(80)), TAI.SCALE);
        convert(t.minus(Duration.millisDuration(60)), TAI.SCALE);
        convert(t.minus(Duration.millisDuration(40)), TAI.SCALE);
        convert(t.minus(Duration.millisDuration(20)), TAI.SCALE);
        convert(t, TAI.SCALE);
        convert(t.plus(Duration.millisDuration(20)), TAI.SCALE);
        System.out.println();
        t = TAI.SCALE.instant(t);
        convert(t.minus(Duration.millisDuration(100)), UTC.SCALE);
        convert(t.minus(Duration.millisDuration(80)), UTC.SCALE);
        convert(t.minus(Duration.millisDuration(60)), UTC.SCALE);
        convert(t.minus(Duration.millisDuration(40)), UTC.SCALE);
        convert(t.minus(Duration.millisDuration(20)), UTC.SCALE);
        convert(t, UTC.SCALE);
        convert(t.plus(Duration.millisDuration(20)), UTC.SCALE);

        // In the following case the UTC values repeat while TAI advances
        System.out.println();
        t = TAI.SCALE.instant(UTC.SCALE.instant(date(1965, 1, 1)));
        convert(t.minus(Duration.millisDuration(120)), UTC.SCALE);
        convert(t.minus(Duration.millisDuration(100)), UTC.SCALE);
        convert(t.minus(Duration.millisDuration(80)), UTC.SCALE);
        convert(t.minus(Duration.millisDuration(60)), UTC.SCALE);
        convert(t.minus(Duration.millisDuration(40)), UTC.SCALE);
        convert(t.minus(Duration.millisDuration(20)), UTC.SCALE);
        convert(t, UTC.SCALE);
        convert(t.plus(Duration.millisDuration(20)), UTC.SCALE);
    }
}
