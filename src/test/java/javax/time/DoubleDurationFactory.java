package javax.time;

public class DoubleDurationFactory extends AbstractDurationFactory<DoubleDuration> {
    public static void main(String[] args) {
        TestDurationPerformance.instance(new DoubleDurationFactory()).run();
    }

    public DoubleDuration seconds(long seconds) {
        return DoubleDuration.seconds(seconds);
    }

    public DoubleDuration millis(long millis) {
        return DoubleDuration.millis(millis);
    }

    public DoubleDuration nanos(long nanos) {
        return DoubleDuration.nanos(nanos);
    }
}
