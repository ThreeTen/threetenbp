package javax.time;

/** Duration based on long milliseconds.
 * Overflow will result in an exception.
 */
public class MillisDuration extends AbstractDuration<MillisDuration> {
    public static final MillisDuration ZERO = new MillisDuration(0);
    private final long milliseconds;

    public static MillisDuration seconds(long seconds) {
        if (seconds == 0) {
            return ZERO;
        }
        return new MillisDuration(MathUtils.safeMultiply(seconds, 1000));
    }

    public static MillisDuration millis(long milliseconds) {
        if (milliseconds == 0) {
            return ZERO;
        }
        return new MillisDuration(milliseconds);
    }

    public static MillisDuration nanos(long nanoseconds) {
        if (nanoseconds == 0) {
            return ZERO;
        }
        return millis(nanoseconds/1000000);
    }

    private MillisDuration(long milliseconds) {
        this.milliseconds = milliseconds;
    }

    public MillisDuration plus(MillisDuration duration) {
        if (milliseconds == 0) {
            return duration;
        }
        if (duration.milliseconds == 0) {
            return this;
        }
        return millis(MathUtils.safeAdd(milliseconds, duration.milliseconds));
    }

    public MillisDuration plusSeconds(long secondsToAdd) {
        if (secondsToAdd == 0) {
            return this;
        }
        return millis(MathUtils.safeAdd(milliseconds, MathUtils.safeMultiply(secondsToAdd, 1000)));
    }

    public MillisDuration plusMillis(long millisToAdd) {
        if (millisToAdd == 0) {
            return this;
        }
        return millis(MathUtils.safeAdd(milliseconds, millisToAdd));
    }

    public MillisDuration plusNanos(long nanosToAdd) {
        if (nanosToAdd == 0) {
            return this;
        }
        return millis(MathUtils.safeAdd(milliseconds, nanosToAdd/1000000));
    }

    public MillisDuration minus(MillisDuration duration) {
        if (duration.milliseconds == 0) {
            return this;
        }
        return millis(MathUtils.safeSubtract(milliseconds, duration.milliseconds));
    }

    public MillisDuration minusSeconds(long secondsToSubtract) {
        if (secondsToSubtract == 0)
            return this;
        return millis(MathUtils.safeSubtract(milliseconds, MathUtils.safeMultiply(secondsToSubtract, 1000)));
    }

    public MillisDuration multipliedBy(long multiplicand) {
        if (multiplicand == 0 || milliseconds == 0) {
            return ZERO;
        }
        if (multiplicand == 1) {
            return this;
        }
        return new MillisDuration(MathUtils.safeMultiply(milliseconds, multiplicand));
    }

    public MillisDuration dividedBy(long divisor) {
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

    public int compareTo(MillisDuration o) {
        if (milliseconds < o.milliseconds)
            return -1;
        if (milliseconds > o.milliseconds)
            return 1;
        return 0;
    }
}
