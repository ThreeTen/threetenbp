package javax.time;

/**
 * Created by IntelliJ IDEA.
 * User: mthornton
 * Date: 16-Dec-2009
 * Time: 20:02:18
 * To change this template use File | Settings | File Templates.
 */
public class UnsafeMillisDurationFactory extends AbstractDurationFactory<UnsafeMillisDuration> {
    public static void main(String[] args) {
        TestDurationPerformance.instance(new UnsafeMillisDurationFactory()).run();
    }

    public UnsafeMillisDuration seconds(long seconds) {
        return UnsafeMillisDuration.seconds(seconds);
    }

    public UnsafeMillisDuration millis(long millis) {
        return UnsafeMillisDuration.millis(millis);
    }

    public UnsafeMillisDuration nanos(long nanos) {
        return UnsafeMillisDuration.nanos(nanos);
    }
}