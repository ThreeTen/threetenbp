/*
 * Written by the members of JCP JSR-310 Expert Group and
 * released to the public domain, as explained at
 * http://creativecommons.org/licenses/publicdomain
 */
package javax.time;

/**
 * A time period representing a number of seconds.
 * <p>
 * Seconds is an immutable period that can only store seconds.
 * It is a type-safe way of representing a number of seconds in an application.
 * <p>
 * Static factory methods allow you to constuct instances.
 * The number of seconds may be queried using getSeconds().
 * Basic mathematical operations are provided - plus(), minus(), multipliedBy(),
 * dividedBy() and negated(), all of which return a new instance
 * <p>
 * Seconds is thread-safe and immutable. 
 * 
 * @author Stephen Colebourne
 */
public final class Seconds implements Comparable<Seconds> {

    /**
     * A constant for zero seconds.
     */
    private static final Seconds ZERO = new Seconds(0);

    /**
     * The number of seconds in the period.
     */
    private final int seconds;

    /**
     * Obtains an instance of <code>Seconds</code>.
     * 
     * @param seconds  the number of seconds the instance will represent
     */
    public static Seconds seconds(int seconds) {
        if (seconds == 0) {
            return ZERO;
        }
        return new Seconds(seconds);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance using a specific numbr of seconds.
     * 
     * @param seconds  the seconds to use
     */
    private Seconds(int seconds) {
        super();
        this.seconds = seconds;
    }

    /**
     * Resolves singletons.
     * 
     * @return the singleton instance
     */
    private Object readResolve() {
        return Seconds.seconds(seconds);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the number of seconds held in this period.
     * 
     * @return the number of seconds
     */
    public int getSeconds() {
        return seconds;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the year represented by this instance.
     * 
     * @param otherSeconds  the other number of seconds, not null
     * @return the comparator value, negative if less, postive if greater
     * @throws NullPointerException if otherSeconds is null
     */
    public int compareTo(Seconds otherSeconds) {
        int thisValue = this.seconds;
        int otherValue = otherSeconds.seconds;
        return (thisValue < otherValue ? -1 : (thisValue == otherValue ? 0 : 1));
    }

    /**
     * Is this seconds instance greater than the specified number of seconds.
     * 
     * @param otherSeconds  the other number of seconds, not null
     * @return true if this number of seconds is greater
     * @throws NullPointerException if otherSeconds is null
     */
    public boolean isGreaterThan(Seconds otherSeconds) {
        return compareTo(otherSeconds) > 0;
    }

    /**
     * Is this seconds instance less than the specified number of seconds.
     * 
     * @param otherSeconds  the other number of seconds, not null
     * @return true if this number of seconds is less
     * @throws NullPointerException if otherSeconds is null
     */
    public boolean isLessThan(Seconds otherSeconds) {
        return compareTo(otherSeconds) < 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Is this instance equal to that specified, evaluating the number of seconds.
     * 
     * @param otherSeconds  the other number of seconds, null return false
     * @return true if this number of seconds is the same as that specified
     */
    public boolean equals(Object otherSeconds) {
        if (otherSeconds instanceof Seconds) {
            return seconds == ((Seconds) otherSeconds).seconds;
        }
        return false;
    }

    /**
     * A hashcode for the seconds object.
     * 
     * @return a suitable hashcode
     */
    public int hashCode() {
        return seconds;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a new instance with the specified number of seconds added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     * 
     * @param seconds  the amount of seconds to add, may be negative
     * @return the new period plus the specified number of seconds
     * @throws ArithmeticException if the result overflows an int
     */
    public Seconds plus(int seconds) {
        if (seconds == 0) {
            return this;
        }
        return Seconds.seconds(MathUtils.safeAdd(this.seconds, seconds));
    }

    /**
     * Returns a new instance with the specified number of seconds added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     * 
     * @param seconds  the amount of seconds to add, may be negative, not null
     * @return the new period plus the specified number of seconds
     * @throws NullPointerException if the seconds to add is null
     * @throws ArithmeticException if the result overflows an int
     */
    public Seconds plus(Seconds seconds) {
        return Seconds.seconds(MathUtils.safeAdd(this.seconds, seconds.seconds));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a new instance with the specified number of seconds taken away.
     * <p>
     * This instance is immutable and unaffected by this method call.
     * 
     * @param seconds  the amount of seconds to take away, may be negative
     * @return the new period minus the specified number of seconds
     * @throws ArithmeticException if the result overflows an int
     */
    public Seconds minus(int seconds) {
        return Seconds.seconds(MathUtils.safeSubtract(this.seconds, seconds));
    }

    /**
     * Returns a new instance with the specified number of seconds taken away.
     * <p>
     * This instance is immutable and unaffected by this method call.
     * 
     * @param seconds  the amount of seconds to take away, may be negative, not null
     * @return the new period minus the specified number of seconds
     * @throws NullPointerException if the seconds to add is null
     * @throws ArithmeticException if the result overflows an int
     */
    public Seconds minus(Seconds seconds) {
        return Seconds.seconds(MathUtils.safeSubtract(this.seconds, seconds.seconds));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a new instance with the seconds multiplied by the specified scalar.
     * <p>
     * This instance is immutable and unaffected by this method call.
     * 
     * @param scalar  the amount to multiply by, may be negative
     * @return the new period multiplied by the specified scalar
     * @throws ArithmeticException if the result overflows an int
     */
    public Seconds multipliedBy(int scalar) {
        return Seconds.seconds(MathUtils.safeMultiply(seconds, scalar));
    }

    /**
     * Returns a new instance with the seconds divided by the specified divisor.
     * The calculation uses integer division, thus 3 divided by 2 is 1.
     * <p>
     * This instance is immutable and unaffected by this method call.
     * 
     * @param divisor  the amount to divide by, may be negative
     * @return the new period divided by the specified divisor
     * @throws ArithmeticException if the divisor is zero
     */
    public Seconds dividedBy(int divisor) {
        if (divisor == 1) {
            return this;
        }
        return Seconds.seconds(seconds / divisor);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a new instance with the seconds value negated.
     * 
     * @return the new period with a negated value
     * @throws ArithmeticException if the result overflows an int
     */
    public Seconds negated() {
        return Seconds.seconds(MathUtils.safeNegate(seconds));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a string representation of the number of seconds.
     * This will be in the format 'PnD' where n is the number of seconds.
     * 
     * @return the number of seconds in ISO8601 string format
     */
    public String toString() {
        return "PT" + seconds + "S";
    }

}
