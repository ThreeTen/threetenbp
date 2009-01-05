package javax.time.scale;

import javax.time.Instant;
import javax.time.TimeScale;
import javax.time.Duration;
import static javax.time.scale.TestScale.*;

/**
 * Created by IntelliJ IDEA.
 * User: mthornton
 * Date: 31-Dec-2008
 * Time: 16:59:13
 * To change this template use File | Settings | File Templates.
 */
public class TestUTC {
    public static void main(String[] args) {
        Instant t = TimeScale.DEFAULT.instant(date(2008, 12, 31)+time(23,59,59), 0);
        Duration increment = Duration.millisDuration(250);
        TimeScale[] scales = {TAI.INSTANCE, UTC.INSTANCE, UTC_NoEpochLeaps.INSTANCE};
        for (int i=0; i<13; i++) {
            System.out.println();
            System.out.println("t="+t);
            for (TimeScale ts: scales) {
                TimeScaleInstant tsi = ts.getTimeScaleInstant(t);
                System.out.print("Scale="+ts.getName()+": "+tsi.getEpochSeconds()+"s + "+tsi.getNanoOfSecond()+"ns");
                if (tsi.isLeapSecondTotalIncluded()) {
                    System.out.print(", includedLeaps="+tsi.getIncludedLeapSeconds());
                }
                if (tsi.getLeapSecond() != 0) {
                    System.out.print(", leap="+tsi.getLeapSecond());
                }
                System.out.println();
            }
            t = t.plus(increment);
        }
    }
}
