package javax.time;

public class BigIntegerDurationFactory extends AbstractDurationFactory<BigIntegerDuration> {
    public static void main(String[] args) {
        TestDurationPerformance.instance(new BigIntegerDurationFactory()).run();
    }

    public BigIntegerDuration seconds(long seconds) {
        return BigIntegerDuration.seconds(seconds);
    }

    public BigIntegerDuration millis(long millis) {
        return BigIntegerDuration.millis(millis);
    }

    public BigIntegerDuration nanos(long nanos) {
        return BigIntegerDuration.nanos(nanos);
    }
}
