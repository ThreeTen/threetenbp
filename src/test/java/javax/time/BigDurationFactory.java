package javax.time;

public class BigDurationFactory extends AbstractDurationFactory<BigDuration> {
    public static void main(String[] args) {
        TestDurationPerformance.instance(new BigDurationFactory()).run();
    }

    public BigDuration seconds(long seconds) {
        return BigDuration.seconds(seconds);
    }

    public BigDuration millis(long millis) {
        return BigDuration.millis(millis);
    }

    public BigDuration nanos(long nanos) {
        return BigDuration.nanos(nanos);
    }

    @Override
    public BigDuration parse(String text) {
        return BigDuration.parse(text);
    }
}
