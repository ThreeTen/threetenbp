package javax.time.scale;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/** Report history of TAI vs UTC in period 1961-01-01 to 1972-01-01.
 */
public class TestUTCHistory {
    public static void main(String[] args) {
        // There is one leap of 50ms
        // then leaps of +-100ms
        // The final jump should be 0.107758s according to wikipedia
        DateFormat fmt = new SimpleDateFormat("yyyy-MMM-dd");
        long endDelta = 0;
        for (AbstractUTC.Entry e: AbstractUTC.UTC_ENTRIES) {
            long startDelta = e.getDelta(e.getStartInclusiveSeconds(), 0);
            checkTaiDelta(e, e.getStartInclusiveSeconds(), startDelta);
            if (endDelta != 0 && startDelta != endDelta) {
                System.out.println("leap: "+(startDelta-endDelta));
            }
            if (!e.hasRate())
                break;
            endDelta = e.getDelta(e.getEndExclusiveSeconds(), 0);
            checkTaiDelta(e, e.getEndExclusiveSeconds(), endDelta);
            System.out.println(fmt.format(new Date(e.getStartInclusiveSeconds()*1000))+" start delta: "+startDelta+", end delta: "+endDelta);
        }
    }

    private static void checkTaiDelta(AbstractUTC.Entry e, long epochSeconds, long delta) {
        long taiSeconds = epochSeconds+(delta/1000000000);
        int taiNanos = (int)(delta%1000000000);
        long taiDelta = e.getDeltaTAI(taiSeconds, taiNanos);
        if (delta != taiDelta) {
            System.out.println("*");
        }
    }
}
