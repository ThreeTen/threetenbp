/*
 * Written by the members of JCP JSR-310 Expert Group and
 * released to the public domain, as explained at
 * http://creativecommons.org/licenses/publicdomain
 */
package javax.time;

/**
 * A time period representing a number of hours.
 * <p>
 * Hours is an immutable period that can only store hours.
 * It is a type-safe way of representing a number of hours in an application.
 * <p>
 * Static factory methods allow you to constuct instances.
 * The number of hours may be queried using getHours().
 * Basic mathematical operations are provided - plus(), minus(), multipliedBy(),
 * dividedBy() and negated(), all of which return a new instance
 * <p>
 * Hours is thread-safe and immutable. 
 * 
 * @author Stephen Colebourne
 */
public final class Hours implements Comparable<Hours> {

    /**
     * A constant for zero hours.
     */
    private static final Hours ZERO = new Hours(0);

    /**
     * The number of hours in the period.
     */
    private final int hours;

    /**
     * Obtains an instance of <code>Hours</code>.
     * 
     * @param hours  the number of hours the instance will represent
     */
    public static Hours hours(int hours) {
        if (hours == 0) {
            return ZERO;
        }
        return new Hours(hours);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance using a specific numbr of hours.
     * 
     * @param hours  the hours to use
     */
    private Hours(int hours) {
        super();
        this.hours = hours;
    }

    /**
     * Resolves singletons.
     * 
     * @return the singleton instance
     */
    private Object readResolve() {
        return Hours.hours(hours);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the number of hours held in this period.
     * 
     * @return the number of hours
     */
    public int getHours() {
        return hours;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the year represented by this instance.
     * 
     * @param otherHours  the other number of hours, not null
     * @return the comparator value, negative if less, postive if greater
     * @throws NullPointerException if otherHours is null
     */
    public int compareTo(Hours otherHours) {
        int thisValue = this.hours;
        int otherValue = otherHours.hours;
        return (thisValue < otherValue ? -1 : (thisValue == otherValue ? 0 : 1));
    }

    /**
     * Is this hours instance greater than the specified number of hours.
     * 
     * @param otherHours  the other number of hours, not null
     * @return true if this number of hours is greater
     * @throws NullPointerException if otherHours is null
     */
    public boolean isGreaterThan(Hours otherHours) {
        return compareTo(otherHours) > 0;
    }

    /**
     * Is this hours instance less than the specified number of hours.
     * 
     * @param otherHours  the other number of hours, not null
     * @return true if this number of hours is less
     * @throws NullPointerException if otherHours is null
     */
    public boolean isLessThan(Hours otherHours) {
        return compareTo(otherHours) < 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Is this instance equal to that specified, evaluating the number of hours.
     * 
     * @param otherHours  the other number of hours, null return false
     * @return true if this number of hours is the same as that specified
     */
    public boolean equals(Object otherHours) {
        if (otherHours instanceof Hours) {
            return hours == ((Hours) otherHours).hours;
        }
        return false;
    }

    /**
     * A hashcode for the hours object.
     * 
     * @return a suitable hashcode
     */
    public int hashCode() {
        return hours;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a new instance with the specified number of hours added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     * 
     * @param hours  the amount of hours to add, may be negative
     * @return the new period plus the specified number of hours
     * @throws ArithmeticException if the result overflows an int
     */
    public Hours plus(int hours) {
        if (hours == 0) {
            return this;
        }
        return Hours.hours(MathUtils.safeAdd(this.hours, hours));
    }

    /**
     * Returns a new instance with the specified number of hours added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     * 
     * @param hours  the amount of hours to add, may be negative, not null
     * @return the new period plus the specified number of hours
     * @throws NullPointerException if the hours to add is null
     * @throws ArithmeticException if the result overflows an int
     */
    public Hours plus(Hours hours) {
        return Hours.hours(MathUtils.safeAdd(this.hours, hours.hours));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a new instance with the specified number of hours taken away.
     * <p>
     * This instance is immutable and unaffected by this method call.
     * 
     * @param hours  the amount of hours to take away, may be negative
     * @return the new period minus the specified number of hours
     * @throws ArithmeticException if the result overflows an int
     */
    public Hours minus(int hours) {
        return Hours.hours(MathUtils.safeSubtract(this.hours, hours));
    }

    /**
     * Returns a new instance with the specified number of hours taken away.
     * <p>
     * This instance is immutable and unaffected by this method call.
     * 
     * @param hours  the amount of hours to take away, may be negative, not null
     * @return the new period minus the specified number of hours
     * @throws NullPointerException if the hours to add is null
     * @throws ArithmeticException if the result overflows an int
     */
    public Hours minus(Hours hours) {
        return Hours.hours(MathUtils.safeSubtract(this.hours, hours.hours));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a new instance with the hours multiplied by the specified scalar.
     * <p>
     * This instance is immutable and unaffected by this method call.
     * 
     * @param scalar  the amount to multiply by, may be negative
     * @return the new period multiplied by the specified scalar
     * @throws ArithmeticException if the result overflows an int
     */
    public Hours multipliedBy(int scalar) {
        return Hours.hours(MathUtils.safeMultiply(hours, scalar));
    }

    /**
     * Returns a new instance with the hours divided by the specified divisor.
     * The calculation uses integer division, thus 3 divided by 2 is 1.
     * <p>
     * This instance is immutable and unaffected by this method call.
     * 
     * @param divisor  the amount to divide by, may be negative
     * @return the new period divided by the specified divisor
     * @throws ArithmeticException if the divisor is zero
     */
    public Hours dividedBy(int divisor) {
        if (divisor == 1) {
            return this;
        }
        return Hours.hours(hours / divisor);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a new instance with the hours value negated.
     * 
     * @return the new period with a negated value
     * @throws ArithmeticException if the result overflows an int
     */
    public Hours negated() {
        return Hours.hours(MathUtils.safeNegate(hours));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a string representation of the number of hours.
     * This will be in the format 'PnD' where n is the number of hours.
     * 
     * @return the number of hours in ISO8601 string format
     */
    public String toString() {
        return "PT" + hours + "H";
    }

}
