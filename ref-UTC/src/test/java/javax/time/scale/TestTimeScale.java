package javax.time.scale;

import javax.time.TimeScale;
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
        time(TAI.INSTANCE, 0);
        time(UTC.INSTANCE, 0);
        time(UTC_NoEpochLeaps.INSTANCE, 0);
        time(TAI.INSTANCE, TestScale.date(2009, 1, 1));
        time(UTC.INSTANCE, TestScale.date(2009, 1, 1));
        time(UTC_NoEpochLeaps.INSTANCE, TestScale.date(2009, 1, 1));
        time(UTC_NoEpochLeaps.INSTANCE, TestScale.date(2008, 12, 31)+ TestScale.time(23,59,59));
        time(UTC_NoEpochLeaps.INSTANCE, TestScale.date(1971, 12, 31)+ TestScale.time(23,59,59));
        time(UTC_NoEpochLeaps.INSTANCE, TestScale.date(1972, 1, 1));

        convertToInstant(UTC.INSTANCE, TimeScaleInstant.instant(date(2008, 12, 31)+TestScale.time(23, 59, 59), 0));
        convertToInstant(UTC.INSTANCE, TimeScaleInstant.leapInstant(date(2008, 12, 31)+TestScale.time(23, 59, 59), 0, 1));
        convertToInstant(UTC.INSTANCE, TimeScaleInstant.instant(date(2009, 1, 1), 0));
        convertToInstant(UTC_NoEpochLeaps.INSTANCE, TimeScaleInstant.instant(date(2008, 12, 31)+TestScale.time(23, 59, 59), 0));
        convertToInstant(UTC_NoEpochLeaps.INSTANCE, TimeScaleInstant.leapInstant(date(2008, 12, 31)+TestScale.time(23, 59, 59), 0, 1));
        convertToInstant(UTC_NoEpochLeaps.INSTANCE, TimeScaleInstant.instant(date(2009, 1, 1), 0));
    }

    private static void convertToInstant(TimeScale scale, TimeScaleInstant tsi) {
        Instant t = scale.instant(tsi);
        System.out.println(tsi.getSimpleEpochSeconds()+"s, "+tsi.getNanoOfSecond()+"ns, leap="+tsi.getLeapSecond()+" ==> "+t);
    }

    private static void time(TimeScale scale, long epochSeconds) {
        Instant t = scale.instant(epochSeconds, 0);
        System.out.print(scale.getName()+" "+epochSeconds+" ==> TAI: "+t);
        if (scale != UTC.INSTANCE) {
            System.out.print("; UTC: "+UTC.INSTANCE.getEpochSeconds(t));
            int nanos = UTC.INSTANCE.getNanoOfSecond(t);
            if (nanos != 0) {
                System.out.print(" + "+nanos+"ns");
            }
        }
        System.out.println();
    }
}
