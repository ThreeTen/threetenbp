package javax.time;

public class DurationFactory extends AbstractDurationFactory<Duration> {
    public static void main(String[] args) {
        TestDurationPerformance.instance(new DurationFactory()).run();
    }
    
    public Duration seconds(long seconds) {
        return Duration.seconds(seconds);
    }

    public Duration millis(long millis) {
        return Duration.millis(millis);
    }

    public Duration nanos(long nanos) {
        return Duration.nanos(nanos);
    }

    @Override
    public Duration parse(String text) {
        return Duration.parse(text);
    }
}
