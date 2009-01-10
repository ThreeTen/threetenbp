package javax.time.scale;

import javax.time.scale.TimeScale;
import javax.time.Instant;
import static javax.time.scale.TestScale.*;

/**
 * Created by IntelliJ IDEA.
 * User: mthornton
 * Date: 24-Dec-2008
 * Time: 20:12:52
 * To change this template use File | Settings | File Templates.
 */
public class TestTimeScale {

    public static void main(String[] args) {
        time(TAI.SCALE, 0);
        time(UTC.SCALE, 0);
        time(UTC_NoEpochLeaps.SCALE, 0);
        time(TAI.SCALE, TestScale.date(2009, 1, 1));
        time(UTC.SCALE, TestScale.date(2009, 1, 1));
        time(UTC_NoEpochLeaps.SCALE, TestScale.date(2009, 1, 1));
        time(UTC_NoEpochLeaps.SCALE, TestScale.date(2008, 12, 31)+ TestScale.time(23,59,59));
        time(UTC_NoEpochLeaps.SCALE, TestScale.date(1971, 12, 31)+ TestScale.time(23,59,59));
        time(UTC_NoEpochLeaps.SCALE, TestScale.date(1972, 1, 1));

        convertToInstant(TimeScaleInstant.instant(UTC.SCALE, date(2008, 12, 31)+TestScale.time(23, 59, 59), 0));
        convertToInstant(TimeScaleInstant.leapInstant(UTC.SCALE, date(2008, 12, 31)+TestScale.time(23, 59, 59), 0, 1));
        convertToInstant(TimeScaleInstant.instant(UTC.SCALE, date(2009, 1, 1), 0));
        convertToInstant(TimeScaleInstant.instant(UTC_NoEpochLeaps.SCALE, date(2008, 12, 31)+TestScale.time(23, 59, 59), 0));
        convertToInstant(TimeScaleInstant.leapInstant(UTC_NoEpochLeaps.SCALE, date(2008, 12, 31)+TestScale.time(23, 59, 59), 0, 1));
        convertToInstant(TimeScaleInstant.instant(UTC_NoEpochLeaps.SCALE, date(2009, 1, 1), 0));
    }

    private static void convertToInstant(TimeScaleInstant tsi) {
        Instant t = Instant.instant(tsi);
        System.out.println(tsi.getSimpleEpochSeconds()+"s, "+tsi.getNanoOfSecond()+"ns, leap="+tsi.getLeapSecond()+" ==> "+t);
    }

    private static void time(TimeScale scale, long epochSeconds) {
        Instant t = Instant.instant(TimeScaleInstant.instant(scale, epochSeconds, 0));
        System.out.print(scale.getName()+" "+epochSeconds+" ==> "+t);
        if (scale != UTC.SCALE) {
            AbstractInstant utc = UTC.SCALE.toScale(t);
            System.out.print("; UTC: "+utc.getEpochSeconds());
            int nanos = utc.getNanoOfSecond();
            if (nanos != 0) {
                System.out.print(" + "+nanos+"ns");
            }
        }
        System.out.println();
    }
}
