package javax.time;

/** Common super class to simplify testing.
 */
public abstract class AbstractDuration<T extends AbstractDuration> implements Comparable<T> {
    public abstract T plus(T duration);
    public abstract T plusSeconds(long secondsToAdd);
    public abstract T plusMillis(long millisToAdd);
    public abstract T plusNanos(long nanosToAdd);
    public abstract T minus(T duration);
    public abstract T minusSeconds(long secondsToSubtract);
    public abstract T multipliedBy(long multiplicand);
    public abstract T dividedBy(long divisor);

    @Override
    public String toString() {
        throw new UnsupportedOperationException();
    }
}
