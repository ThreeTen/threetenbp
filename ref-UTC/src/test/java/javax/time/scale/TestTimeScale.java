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
        time(TAI.SCALE, 0);
        time(TrueUTC.SCALE, 0);
        time(TAI.SCALE, TestScale.date(2009, 1, 1));
        time(TrueUTC.SCALE, TestScale.date(2009, 1, 1));
        time(TrueUTC.SCALE, TestScale.date(2009, 1, 1));
        time(TrueUTC.SCALE, TestScale.date(2008, 12, 31)+ TestScale.time(23,59,59));
        time(TrueUTC.SCALE, TestScale.date(1971, 12, 31)+ TestScale.time(23,59,59));
        time(TrueUTC.SCALE, TestScale.date(1972, 1, 1));

        convertToInstant(TrueUTC.SCALE.instant(date(2008, 12, 31) + TestScale.time(23, 59, 59), 0));
        convertToInstant(TrueUTC.SCALE.instant(date(2008, 12, 31) + TestScale.time(23, 59, 59), 0, 1));
        convertToInstant(TrueUTC.SCALE.instant(date(2009, 1, 1), 0));
    }

    private static void convertToInstant(Instant tsi) {
        Instant t = Instant.instant(tsi);
        System.out.println(tsi.getEpochSeconds()+"s, "+tsi.getNanoOfSecond()+"ns, leap="+tsi.getLeapSecond()+" ==> "+t);
    }

    private static void time(TimeScale scale, long epochSeconds) {
        Instant t = Instant.instant(scale.instant(epochSeconds, 0));
        System.out.print(scale.getName()+" "+epochSeconds+" ==> "+t);
        if (scale != TrueUTC.SCALE) {
            Instant utc = TrueUTC.SCALE.instant(t);
            System.out.print("; UTC: "+utc.getEpochSeconds());
            int nanos = utc.getNanoOfSecond();
            if (nanos != 0) {
                System.out.print(" + "+nanos+"ns");
            }
        }
        System.out.println();
    }
}
