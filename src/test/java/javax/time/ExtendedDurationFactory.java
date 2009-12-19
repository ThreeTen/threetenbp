package javax.time;

public class ExtendedDurationFactory extends AbstractDurationFactory<ExtendedDuration> {
    public static void main(String[] args) {
        TestDurationPerformance.instance(new ExtendedDurationFactory()).run();
    }

    public ExtendedDuration seconds(long seconds) {
        return ExtendedDuration.seconds(seconds);
    }

    public ExtendedDuration millis(long millis) {
        return ExtendedDuration.millis(millis);
    }

    public ExtendedDuration nanos(long nanos) {
        return ExtendedDuration.nanos(nanos);
    }

    @Override
    public ExtendedDuration parse(String text) {
        return ExtendedDuration.parse(text);
    }
}
