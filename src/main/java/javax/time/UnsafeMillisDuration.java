package javax.time;

/** Duration based on long milliseconds but without overflow tests.
 */
public class UnsafeMillisDuration extends AbstractDuration<UnsafeMillisDuration> {
    public static final UnsafeMillisDuration ZERO = new UnsafeMillisDuration(0);
    private final long milliseconds;

    public static UnsafeMillisDuration seconds(long seconds) {
        if (seconds == 0) {
            return ZERO;
        }
        return new UnsafeMillisDuration(seconds * 1000);
    }

    public static UnsafeMillisDuration millis(long milliseconds) {
        if (milliseconds == 0) {
            return ZERO;
        }
        return new UnsafeMillisDuration(milliseconds);
    }

    public static UnsafeMillisDuration nanos(long nanoseconds) {
        if (nanoseconds == 0) {
            return ZERO;
        }
        return millis(nanoseconds/1000000);
    }

    private UnsafeMillisDuration(long milliseconds) {
        this.milliseconds = milliseconds;
    }

    public UnsafeMillisDuration plus(UnsafeMillisDuration duration) {
        if (milliseconds == 0) {
            return duration;
        }
        if (duration.milliseconds == 0) {
            return this;
        }
        return millis(milliseconds + duration.milliseconds);
    }

    public UnsafeMillisDuration plusSeconds(long secondsToAdd) {
        if (secondsToAdd == 0) {
            return this;
        }
        return millis(milliseconds+secondsToAdd * 1000);
    }

    public UnsafeMillisDuration plusMillis(long millisToAdd) {
        if (millisToAdd == 0) {
            return this;
        }
        return millis(milliseconds + millisToAdd);
    }

    public UnsafeMillisDuration plusNanos(long nanosToAdd) {
        if (nanosToAdd == 0) {
            return this;
        }
        return millis(milliseconds + nanosToAdd/1000000);
    }

    public UnsafeMillisDuration minus(UnsafeMillisDuration duration) {
        if (duration.milliseconds == 0) {
            return this;
        }
        return millis(milliseconds - duration.milliseconds);
    }

    public UnsafeMillisDuration minusSeconds(long secondsToSubtract) {
        if (secondsToSubtract == 0)
            return this;
        return millis(milliseconds - secondsToSubtract * 1000);
    }

    public UnsafeMillisDuration multipliedBy(long multiplicand) {
        if (multiplicand == 0 || milliseconds == 0) {
            return ZERO;
        }
        if (multiplicand == 1) {
            return this;
        }
        return new UnsafeMillisDuration(milliseconds * multiplicand);
    }

    public UnsafeMillisDuration dividedBy(long divisor) {
        if (divisor == 0) {
            throw new ArithmeticException("Divide by zero");
        }
        if (milliseconds == 0 || divisor == 1) {
            return this;
        }
        if (divisor == -1 && milliseconds == Long.MIN_VALUE) {
            throw new ArithmeticException("Overflow in division");
        }
        return millis(milliseconds/divisor);
    }

    public int compareTo(UnsafeMillisDuration o) {
        if (milliseconds < o.milliseconds)
            return -1;
        if (milliseconds > o.milliseconds)
            return 1;
        return 0;
    }
}