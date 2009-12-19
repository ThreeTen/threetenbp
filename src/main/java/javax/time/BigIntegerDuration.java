package javax.time;

import java.math.BigInteger;

/** Duration based on BigInteger nanoseconds.
 */
public class BigIntegerDuration extends AbstractDuration<BigIntegerDuration> {
    public static final BigIntegerDuration ZERO = new BigIntegerDuration(BigInteger.ZERO);
    private final BigInteger nanoseconds;
    private static final BigInteger NANOSECONDS_PER_SECOND = BigInteger.valueOf(1000000000);
    private static final BigInteger NANOSECONDS_PER_MILLISECOND = BigInteger.valueOf(1000000);

    public static BigIntegerDuration seconds(long seconds) {
        if (seconds == 0) {
            return ZERO;
        }
        return new BigIntegerDuration(BigInteger.valueOf(seconds).multiply(NANOSECONDS_PER_SECOND));
    }

    public static BigIntegerDuration millis(long millis) {
        if (millis == 0) {
            return ZERO;
        }
        return new BigIntegerDuration(BigInteger.valueOf(millis).multiply(NANOSECONDS_PER_MILLISECOND));
    }

    public static BigIntegerDuration nanos(long nanos) {
        if (nanos == 0) {
            return ZERO;
        }
        return new BigIntegerDuration(BigInteger.valueOf(nanos));
    }

    public static BigIntegerDuration nanos(BigInteger nanos) {
        if (nanos.equals(BigInteger.ZERO)) {
            return ZERO;
        }
        return new BigIntegerDuration(nanos);
    }

    private BigIntegerDuration(BigInteger nanoseconds) {
        this.nanoseconds = nanoseconds;
    }

    public BigIntegerDuration plus(BigIntegerDuration duration) {
        return nanos(nanoseconds.add(duration.nanoseconds));
    }

    public BigIntegerDuration plusSeconds(long secondsToAdd) {
        if (secondsToAdd == 0) {
            return this;
        }
        return nanos(nanoseconds.add(BigInteger.valueOf(secondsToAdd).multiply(NANOSECONDS_PER_SECOND)));
    }

    public BigIntegerDuration plusMillis(long millisToAdd) {
        if (millisToAdd == 0) {
            return this;
        }
        return nanos(nanoseconds.add(BigInteger.valueOf(millisToAdd).multiply(NANOSECONDS_PER_MILLISECOND)));
    }

    public BigIntegerDuration plusNanos(long nanosToAdd) {
        if (nanosToAdd == 0) {
            return this;
        }
        return nanos(nanoseconds.add(BigInteger.valueOf(nanosToAdd)));
    }

    public BigIntegerDuration minus(BigIntegerDuration duration) {
        return nanos(nanoseconds.subtract(duration.nanoseconds));
    }

    public BigIntegerDuration minusSeconds(long secondsToSubtract) {
        if (secondsToSubtract == 0) {
            return this;
        }
        return nanos(nanoseconds.subtract(BigInteger.valueOf(secondsToSubtract).multiply(NANOSECONDS_PER_SECOND)));
    }

    public BigIntegerDuration multipliedBy(long multiplicand) {
        if (multiplicand == 0) {
            return ZERO;
        }
        if (multiplicand == 1) {
            return this;
        }
        return nanos(nanoseconds.multiply(BigInteger.valueOf(multiplicand)));
    }

    public BigIntegerDuration dividedBy(long divisor) {
        if (divisor == 1) {
            return this;
        }
        return nanos(nanoseconds.divide(BigInteger.valueOf(divisor)));
    }

    public int compareTo(BigIntegerDuration o) {
        return nanoseconds.compareTo(o.nanoseconds);
    }
}
