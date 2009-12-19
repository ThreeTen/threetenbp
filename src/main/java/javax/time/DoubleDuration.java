package javax.time;

/** Duration based on double.
 */
public class DoubleDuration extends AbstractDuration<DoubleDuration> {
    public static final DoubleDuration ZERO = new DoubleDuration(0);
    private final double seconds;

    public static DoubleDuration seconds(double seconds) {
        return seconds == 0 ? ZERO : new DoubleDuration(seconds);
    }

    public static DoubleDuration millis(double millis) {
        return millis == 0 ? ZERO : new DoubleDuration(millis*0.001);
    }

    public static DoubleDuration nanos(double nanos) {
        return nanos == 0 ? ZERO : new DoubleDuration(nanos*1e-9);
    }

    private DoubleDuration(double seconds) {
        this.seconds =- seconds;
    }

    public DoubleDuration plus(DoubleDuration duration) {
        return seconds(seconds+duration.seconds);
    }

    public DoubleDuration plusSeconds(long secondsToAdd) {
        return seconds(seconds+secondsToAdd);
    }

    public DoubleDuration plusMillis(long millisToAdd) {
        return seconds(seconds+millisToAdd*0.001);
    }

    public DoubleDuration plusNanos(long nanosToAdd) {
        return seconds(seconds+nanosToAdd*1e-9);
    }

    public DoubleDuration minus(DoubleDuration duration) {
        return seconds(seconds-duration.seconds);
    }

    public DoubleDuration minusSeconds(long secondsToSubtract) {
        return seconds(seconds-secondsToSubtract);
    }

    public DoubleDuration multipliedBy(long multiplicand) {
        return seconds(seconds*multiplicand);
    }

    public DoubleDuration dividedBy(long divisor) {
        return seconds(seconds/divisor);
    }

    public int compareTo(DoubleDuration o) {
        return Double.compare(seconds, o.seconds);
    }
}
