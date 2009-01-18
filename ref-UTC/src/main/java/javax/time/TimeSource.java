package javax.time;

import javax.time.scale.TimeScale;
import javax.time.scale.AbstractInstant;
import javax.time.scale.UTC_NoLeaps;
import javax.time.impl.WindowsSystemTime;

/**
 * Created by IntelliJ IDEA.
 * User: mthornton
 * Date: 18-Jan-2009
 * Time: 11:01:08
 * To change this template use File | Settings | File Templates.
 */
public abstract class TimeSource {

    /** TimeSource based on System.currentTimeMillis() */
    public static TimeSource java() {
        return JavaTimeSource.SOURCE;
    }

    /** Operating system based time source.
     */
    public static TimeSource system() {
        return System.SOURCE;
    }

    public abstract TimeScale<?> getScale();
    public abstract AbstractInstant<?> instant();

    private static class JavaTimeSource extends TimeSource {
        static final JavaTimeSource SOURCE = new JavaTimeSource();
        public TimeScale<?> getScale() {
            return UTC_NoLeaps.SCALE;
        }

        public AbstractInstant<?> instant() {
            long millis = java.lang.System.currentTimeMillis();
            long seconds = millis / 1000;
            int nanos = (int)(millis%1000);
            if (nanos < 0) {
                nanos += 1000;
                seconds--;
            }
            return UTC_NoLeaps.SCALE.instant(seconds, nanos*1000000);
        }
    }

    private static class System {
        static final TimeSource SOURCE;

        static {
            // TODO: Need to identify the operating system and select an appropriate source
            SOURCE = WindowsSystemTime.SOURCE;
        }
    }
}
