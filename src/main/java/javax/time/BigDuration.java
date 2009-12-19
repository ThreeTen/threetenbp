package javax.time;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.math.BigInteger;

/** Duration based on BigDecimal.
 * The maximum scale is 9, corresponding to nanosecond precision.
 */
public class BigDuration extends AbstractDuration<BigDuration> implements Comparable<BigDuration>, Serializable {
    public static final BigDuration ZERO = new BigDuration(BigDecimal.ZERO);
    public static final BigDuration STANDARD_MINUTE = seconds(60);
    public static final BigDuration STANDARD_HOUR = seconds(3600);
    public static final BigDuration STANDARD_DAY = seconds(86400);
    private static final boolean ALWAYS_RESCALE = false;

    private static final long serialVersionUID = 1L;
    /** Rounding method to use for inexact computation.
     * This can be any mode except RoundingMode.UNNECESSARY
     */
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_EVEN;
    private static final int MAX_SCALE = 9;

    private final BigDecimal seconds;
    private int hashCode;

    public static BigDuration seconds(long seconds) {
        return seconds == 0 ? ZERO : new BigDuration(BigDecimal.valueOf(seconds));
    }

    public static BigDuration seconds(BigDecimal seconds) {
        if (seconds.equals(BigDecimal.ZERO))
            return ZERO;
        if (ALWAYS_RESCALE || seconds.scale() > MAX_SCALE) {
            seconds = seconds.setScale(MAX_SCALE, ROUNDING_MODE);
        }
        // TODO: if required check upper/lower bounds
        return new BigDuration(seconds);
    }

    public static BigDuration seconds(BigInteger seconds) {
        if (seconds.equals(BigInteger.ZERO))
            return ZERO;
        return new BigDuration(new BigDecimal(seconds));
    }

    public static BigDuration millis(long millis) {
        return millis == 0 ? ZERO : new BigDuration(BigDecimal.valueOf(millis, 3));
    }

    public static BigDuration nanos(long nanos) {
        return nanos == 0 ? ZERO : new BigDuration(BigDecimal.valueOf(nanos, 9));
    }

    public static BigDuration standardMinutes(long minutes) {
        return STANDARD_MINUTE.multipliedBy(minutes);
    }

    public static BigDuration standardHours(long hours) {
        return STANDARD_HOUR.multipliedBy(hours);
    }

    public static BigDuration standardDays(long days) {
        return STANDARD_DAY.multipliedBy(days);
    }

    public static BigDuration parse(String text) {
        if (!text.regionMatches(true, 0, "PT", 0, 2) ||
                Character.toUpperCase(text.charAt(text.length()-1)) != 'S') {
            throw new IllegalArgumentException("Duration could not be parsed");
        }
        return seconds(new BigDecimal(text.substring(2, text.length()-1)));
    }

    private BigDuration(BigDecimal seconds) {
        this.seconds = seconds;
    }

    private Object readResolve() {
        if (seconds.compareTo(BigDecimal.ZERO) == 0) {
            return ZERO;
        }
        return this;
    }

    @Override
    public int hashCode() {
        /* We want hashCode to match equals, and unlike BigDecimal this compares numerical value without
        regard to scale.
         */
        if (hashCode == 0) {
            BigDecimal s = seconds;
            if (s.scale() != MAX_SCALE)
                s = s.setScale(MAX_SCALE);
            int h = s.hashCode();
            if (h == 0)
                h = 1;
            hashCode = h;
        }
        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this || obj instanceof BigDuration && seconds.compareTo(((BigDuration)obj).seconds) == 0;
    }

    public int compareTo(BigDuration o) {
        return seconds.compareTo(o.seconds);
    }

    public BigDecimal getSeconds() {
        return seconds;
    }

    public BigDuration plus(BigDuration duration) {
        return seconds(seconds.add(duration.seconds));
    }

    public BigDuration plusSeconds(long secondsToAdd) {
        if (secondsToAdd == 0) {
            return this;
        }
        return seconds(seconds.add(BigDecimal.valueOf(secondsToAdd)));
    }

    public BigDuration plusMillis(long millisToAdd) {
        if (millisToAdd == 0)
            return this;
        return seconds(seconds.add(BigDecimal.valueOf(millisToAdd, 3)));
    }

    public BigDuration plusNanos(long nanosToAdd) {
        if (nanosToAdd == 0)
            return this;
        return seconds(seconds.add(BigDecimal.valueOf(nanosToAdd, 9)));
    }

    public BigDuration minus(BigDuration duration) {
        return seconds(seconds.subtract(duration.seconds));
    }

    public BigDuration minusSeconds(long secondsToSubtract) {
        if (secondsToSubtract == 0) {
            return this;
        }
        return seconds(seconds.subtract(BigDecimal.valueOf(secondsToSubtract)));
    }

    public BigDuration multipliedBy(long multiplicand) {
        return seconds(seconds.multiply(BigDecimal.valueOf(multiplicand)));
    }

    public BigDuration multipliedBy(BigDecimal multiplicand) {
        return seconds(seconds.multiply(multiplicand));
    }

    public BigDuration dividedBy(long divisor) {
        return seconds(seconds.divide(BigDecimal.valueOf(divisor), MAX_SCALE, ROUNDING_MODE));
    }

    public BigDuration dividedBy(BigDecimal divisor) {
        return seconds(seconds.divide(divisor, MAX_SCALE, ROUNDING_MODE));
    }

    @Override
    public String toString() {
        return "PT"+seconds.toPlainString()+"S";
    }
}
