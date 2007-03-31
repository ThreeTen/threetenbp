/*
 * Written by the members of JCP JSR-310 Expert Group and
 * released to the public domain, as explained at
 * http://creativecommons.org/licenses/publicdomain
 */
package javax.time;

/**
 * A time period representing a number of days.
 * <p>
 * Days is an immutable period that can only store days.
 * It is a type-safe way of representing a number of days in an application.
 * <p>
 * Static factory methods allow you to constuct instances.
 * The number of days may be queried using getDays().
 * Basic mathematical operations are provided - plus(), minus(), multipliedBy(),
 * dividedBy() and negated(), all of which return a new instance
 * <p>
 * Days is thread-safe and immutable. 
 * 
 * @author Stephen Colebourne
 */
public final class Days implements Comparable<Days> {

    /**
     * A constant for zero days.
     */
    private static final Days ZERO = new Days(0);

    /**
     * The number of days in the period.
     */
    private final int days;

    /**
     * Obtains an instance of <code>Days</code>.
     * 
     * @param days  the number of days the instance will represent
     */
    public static Days days(int days) {
        if (days == 0) {
            return ZERO;
        }
        return new Days(days);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance using a specific numbr of days.
     * 
     * @param days  the days to use
     */
    private Days(int days) {
        super();
        this.days = days;
    }

    /**
     * Resolves singletons.
     * 
     * @return the singleton instance
     */
    private Object readResolve() {
        return Days.days(days);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the number of days held in this period.
     * 
     * @return the number of days
     */
    public int getDays() {
        return days;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the year represented by this instance.
     * 
     * @param otherDays  the other number of days, not null
     * @return the comparator value, negative if less, postive if greater
     * @throws NullPointerException if otherDays is null
     */
    public int compareTo(Days otherDays) {
        int thisValue = this.days;
        int otherValue = otherDays.days;
        return (thisValue < otherValue ? -1 : (thisValue == otherValue ? 0 : 1));
    }

    /**
     * Is this days instance greater than the specified number of days.
     * 
     * @param otherDays  the other number of days, not null
     * @return true if this number of days is greater
     * @throws NullPointerException if otherDays is null
     */
    public boolean isGreaterThan(Days otherDays) {
        return compareTo(otherDays) > 0;
    }

    /**
     * Is this days instance less than the specified number of days.
     * 
     * @param otherDays  the other number of days, not null
     * @return true if this number of days is less
     * @throws NullPointerException if otherDays is null
     */
    public boolean isLessThan(Days otherDays) {
        return compareTo(otherDays) < 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Is this instance equal to that specified, evaluating the number of days.
     * 
     * @param otherDays  the other number of days, null return false
     * @return true if this number of days is the same as that specified
     */
    public boolean equals(Object otherDays) {
        if (otherDays instanceof Days) {
            return days == ((Days) otherDays).days;
        }
        return false;
    }

    /**
     * A hashcode for the days object.
     * 
     * @return a suitable hashcode
     */
    public int hashCode() {
        return days;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a new instance with the specified number of days added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     * 
     * @param days  the amount of days to add, may be negative
     * @return the new period plus the specified number of days
     * @throws ArithmeticException if the result overflows an int
     */
    public Days plus(int days) {
        if (days == 0) {
            return this;
        }
        return Days.days(MathUtils.safeAdd(this.days, days));
    }

    /**
     * Returns a new instance with the specified number of days added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     * 
     * @param days  the amount of days to add, may be negative, not null
     * @return the new period plus the specified number of days
     * @throws NullPointerException if the days to add is null
     * @throws ArithmeticException if the result overflows an int
     */
    public Days plus(Days days) {
        return Days.days(MathUtils.safeAdd(this.days, days.days));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a new instance with the specified number of days taken away.
     * <p>
     * This instance is immutable and unaffected by this method call.
     * 
     * @param days  the amount of days to take away, may be negative
     * @return the new period minus the specified number of days
     * @throws ArithmeticException if the result overflows an int
     */
    public Days minus(int days) {
        return Days.days(MathUtils.safeSubtract(this.days, days));
    }

    /**
     * Returns a new instance with the specified number of days taken away.
     * <p>
     * This instance is immutable and unaffected by this method call.
     * 
     * @param days  the amount of days to take away, may be negative, not null
     * @return the new period minus the specified number of days
     * @throws NullPointerException if the days to add is null
     * @throws ArithmeticException if the result overflows an int
     */
    public Days minus(Days days) {
        return Days.days(MathUtils.safeSubtract(this.days, days.days));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a new instance with the days multiplied by the specified scalar.
     * <p>
     * This instance is immutable and unaffected by this method call.
     * 
     * @param scalar  the amount to multiply by, may be negative
     * @return the new period multiplied by the specified scalar
     * @throws ArithmeticException if the result overflows an int
     */
    public Days multipliedBy(int scalar) {
        return Days.days(MathUtils.safeMultiply(days, scalar));
    }

    /**
     * Returns a new instance with the days divided by the specified divisor.
     * The calculation uses integer division, thus 3 divided by 2 is 1.
     * <p>
     * This instance is immutable and unaffected by this method call.
     * 
     * @param divisor  the amount to divide by, may be negative
     * @return the new period divided by the specified divisor
     * @throws ArithmeticException if the divisor is zero
     */
    public Days dividedBy(int divisor) {
        if (divisor == 1) {
            return this;
        }
        return Days.days(days / divisor);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a new instance with the days value negated.
     * 
     * @return the new period with a negated value
     * @throws ArithmeticException if the result overflows an int
     */
    public Days negated() {
        return Days.days(MathUtils.safeNegate(days));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a string representation of the number of days.
     * This will be in the format 'PnD' where n is the number of days.
     * 
     * @return the number of days in ISO8601 string format
     */
    public String toString() {
        return "P" + days + "D";
    }

}
