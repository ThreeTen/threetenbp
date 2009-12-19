package javax.time;

/**
 * Created by IntelliJ IDEA.
 * User: mthornton
 * Date: 16-Dec-2009
 * Time: 20:02:18
 * To change this template use File | Settings | File Templates.
 */
public class MillisDurationFactory extends AbstractDurationFactory<MillisDuration> {
    public static void main(String[] args) {
        TestDurationPerformance.instance(new MillisDurationFactory()).run();
    }

    public MillisDuration seconds(long seconds) {
        return MillisDuration.seconds(seconds);
    }

    public MillisDuration millis(long millis) {
        return MillisDuration.millis(millis);
    }

    public MillisDuration nanos(long nanos) {
        return MillisDuration.nanos(nanos);
    }
}
