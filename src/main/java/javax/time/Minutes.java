/*
 * Written by the members of JCP JSR-310 Expert Group and
 * released to the public domain, as explained at
 * http://creativecommons.org/licenses/publicdomain
 */
package javax.time;

/**
 * A time period representing a number of minutes.
 * <p>
 * Minutes is an immutable period that can only store minutes.
 * It is a type-safe way of representing a number of minutes in an application.
 * <p>
 * Static factory methods allow you to constuct instances.
 * The number of minutes may be queried using getMinutes().
 * Basic mathematical operations are provided - plus(), minus(), multipliedBy(),
 * dividedBy() and negated(), all of which return a new instance
 * <p>
 * Minutes is thread-safe and immutable. 
 * 
 * @author Stephen Colebourne
 */
public final class Minutes implements Comparable<Minutes> {

    /**
     * A constant for zero minutes.
     */
    private static final Minutes ZERO = new Minutes(0);

    /**
     * The number of minutes in the period.
     */
    private final int minutes;

    /**
     * Obtains an instance of <code>Minutes</code>.
     * 
     * @param minutes  the number of minutes the instance will represent
     */
    public static Minutes minutes(int minutes) {
        if (minutes == 0) {
            return ZERO;
        }
        return new Minutes(minutes);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance using a specific numbr of minutes.
     * 
     * @param minutes  the minutes to use
     */
    private Minutes(int minutes) {
        super();
        this.minutes = minutes;
    }

    /**
     * Resolves singletons.
     * 
     * @return the singleton instance
     */
    private Object readResolve() {
        return Minutes.minutes(minutes);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the number of minutes held in this period.
     * 
     * @return the number of minutes
     */
    public int getMinutes() {
        return minutes;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the year represented by this instance.
     * 
     * @param otherMinutes  the other number of minutes, not null
     * @return the comparator value, negative if less, postive if greater
     * @throws NullPointerException if otherMinutes is null
     */
    public int compareTo(Minutes otherMinutes) {
        int thisValue = this.minutes;
        int otherValue = otherMinutes.minutes;
        return (thisValue < otherValue ? -1 : (thisValue == otherValue ? 0 : 1));
    }

    /**
     * Is this minutes instance greater than the specified number of minutes.
     * 
     * @param otherMinutes  the other number of minutes, not null
     * @return true if this number of minutes is greater
     * @throws NullPointerException if otherMinutes is null
     */
    public boolean isGreaterThan(Minutes otherMinutes) {
        return compareTo(otherMinutes) > 0;
    }

    /**
     * Is this minutes instance less than the specified number of minutes.
     * 
     * @param otherMinutes  the other number of minutes, not null
     * @return true if this number of minutes is less
     * @throws NullPointerException if otherMinutes is null
     */
    public boolean isLessThan(Minutes otherMinutes) {
        return compareTo(otherMinutes) < 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Is this instance equal to that specified, evaluating the number of minutes.
     * 
     * @param otherMinutes  the other number of minutes, null return false
     * @return true if this number of minutes is the same as that specified
     */
    public boolean equals(Object otherMinutes) {
        if (otherMinutes instanceof Minutes) {
            return minutes == ((Minutes) otherMinutes).minutes;
        }
        return false;
    }

    /**
     * A hashcode for the minutes object.
     * 
     * @return a suitable hashcode
     */
    public int hashCode() {
        return minutes;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a new instance with the specified number of minutes added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     * 
     * @param minutes  the amount of minutes to add, may be negative
     * @return the new period plus the specified number of minutes
     * @throws ArithmeticException if the result overflows an int
     */
    public Minutes plus(int minutes) {
        if (minutes == 0) {
            return this;
        }
        return Minutes.minutes(MathUtils.safeAdd(this.minutes, minutes));
    }

    /**
     * Returns a new instance with the specified number of minutes added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     * 
     * @param minutes  the amount of minutes to add, may be negative, not null
     * @return the new period plus the specified number of minutes
     * @throws NullPointerException if the minutes to add is null
     * @throws ArithmeticException if the result overflows an int
     */
    public Minutes plus(Minutes minutes) {
        return Minutes.minutes(MathUtils.safeAdd(this.minutes, minutes.minutes));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a new instance with the specified number of minutes taken away.
     * <p>
     * This instance is immutable and unaffected by this method call.
     * 
     * @param minutes  the amount of minutes to take away, may be negative
     * @return the new period minus the specified number of minutes
     * @throws ArithmeticException if the result overflows an int
     */
    public Minutes minus(int minutes) {
        return Minutes.minutes(MathUtils.safeSubtract(this.minutes, minutes));
    }

    /**
     * Returns a new instance with the specified number of minutes taken away.
     * <p>
     * This instance is immutable and unaffected by this method call.
     * 
     * @param minutes  the amount of minutes to take away, may be negative, not null
     * @return the new period minus the specified number of minutes
     * @throws NullPointerException if the minutes to add is null
     * @throws ArithmeticException if the result overflows an int
     */
    public Minutes minus(Minutes minutes) {
        return Minutes.minutes(MathUtils.safeSubtract(this.minutes, minutes.minutes));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a new instance with the minutes multiplied by the specified scalar.
     * <p>
     * This instance is immutable and unaffected by this method call.
     * 
     * @param scalar  the amount to multiply by, may be negative
     * @return the new period multiplied by the specified scalar
     * @throws ArithmeticException if the result overflows an int
     */
    public Minutes multipliedBy(int scalar) {
        return Minutes.minutes(MathUtils.safeMultiply(minutes, scalar));
    }

    /**
     * Returns a new instance with the minutes divided by the specified divisor.
     * The calculation uses integer division, thus 3 divided by 2 is 1.
     * <p>
     * This instance is immutable and unaffected by this method call.
     * 
     * @param divisor  the amount to divide by, may be negative
     * @return the new period divided by the specified divisor
     * @throws ArithmeticException if the divisor is zero
     */
    public Minutes dividedBy(int divisor) {
        if (divisor == 1) {
            return this;
        }
        return Minutes.minutes(minutes / divisor);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a new instance with the minutes value negated.
     * 
     * @return the new period with a negated value
     * @throws ArithmeticException if the result overflows an int
     */
    public Minutes negated() {
        return Minutes.minutes(MathUtils.safeNegate(minutes));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a string representation of the number of minutes.
     * This will be in the format 'PnD' where n is the number of minutes.
     * 
     * @return the number of minutes in ISO8601 string format
     */
    public String toString() {
        return "PT" + minutes + "M";
    }

}
