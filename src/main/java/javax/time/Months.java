/*
 * Copyright (c) 2007, Stephen Colebourne & Michael Nascimento Santos
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  * Neither the name of JSR-310 nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package javax.time;

/**
 * A time period representing a number of months.
 * <p>
 * Months is an immutable period that can only store months.
 * It is a type-safe way of representing a number of months in an application.
 * <p>
 * Static factory methods allow you to constuct instances.
 * The number of months may be queried using getMonths().
 * Basic mathematical operations are provided - plus(), minus(), multipliedBy(),
 * dividedBy() and negated(), all of which return a new instance
 * <p>
 * Months is thread-safe and immutable. 
 * 
 * @author Stephen Colebourne
 */
public final class Months implements Comparable<Months> {

    /**
     * A constant for zero months.
     */
    private static final Months ZERO = new Months(0);

    /**
     * The number of months in the period.
     */
    private final int months;

    /**
     * Obtains an instance of <code>Months</code>.
     * 
     * @param months  the number of months the instance will represent
     */
    public static Months months(int months) {
        if (months == 0) {
            return ZERO;
        }
        return new Months(months);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance using a specific numbr of months.
     * 
     * @param months  the months to use
     */
    private Months(int months) {
        super();
        this.months = months;
    }

    /**
     * Resolves singletons.
     * 
     * @return the singleton instance
     */
    private Object readResolve() {
        return Months.months(months);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the number of months held in this period.
     * 
     * @return the number of months
     */
    public int getMonths() {
        return months;
    }

    //-----------------------------------------------------------------------
    /**
     * Compares the number of months in this instance to another instance.
     * 
     * @param otherMonths  the other number of months, not null
     * @return the comparator value, negative if less, postive if greater
     * @throws NullPointerException if otherMonths is null
     */
    public int compareTo(Months otherMonths) {
        int thisValue = this.months;
        int otherValue = otherMonths.months;
        return (thisValue < otherValue ? -1 : (thisValue == otherValue ? 0 : 1));
    }

    /**
     * Is the number of months in this instance greater than that in
     * another instance.
     * 
     * @param otherMonths  the other number of months, not null
     * @return true if this number of months is greater
     * @throws NullPointerException if otherMonths is null
     */
    public boolean isGreaterThan(Months otherMonths) {
        return compareTo(otherMonths) > 0;
    }

    /**
     * Is the number of months in this instance less than that in
     * another instance.
     * 
     * @param otherMonths  the other number of months, not null
     * @return true if this number of months is less
     * @throws NullPointerException if otherMonths is null
     */
    public boolean isLessThan(Months otherMonths) {
        return compareTo(otherMonths) < 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Is this instance equal to that specified, evaluating the number of months.
     * 
     * @param otherMonths  the other number of months, null returns false
     * @return true if this number of months is the same as that specified
     */
    public boolean equals(Object otherMonths) {
        if (this == otherMonths) {
           return true;
        }
        if (otherMonths instanceof Months) {
            return months == ((Months) otherMonths).months;
        }
        return false;
    }

    /**
     * A hashcode for the months object.
     * 
     * @return a suitable hashcode
     */
    public int hashCode() {
        return months;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a new instance with the specified number of months added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     * 
     * @param months  the amount of months to add, may be negative
     * @return the new period plus the specified number of months
     * @throws ArithmeticException if the result overflows an int
     */
    public Months plus(int months) {
        if (months == 0) {
            return this;
        }
        return Months.months(MathUtils.safeAdd(this.months, months));
    }

    /**
     * Returns a new instance with the specified number of months added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     * 
     * @param months  the amount of months to add, may be negative, not null
     * @return the new period plus the specified number of months
     * @throws NullPointerException if the months to add is null
     * @throws ArithmeticException if the result overflows an int
     */
    public Months plus(Months months) {
        return Months.months(MathUtils.safeAdd(this.months, months.months));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a new instance with the specified number of months taken away.
     * <p>
     * This instance is immutable and unaffected by this method call.
     * 
     * @param months  the amount of months to take away, may be negative
     * @return the new period minus the specified number of months
     * @throws ArithmeticException if the result overflows an int
     */
    public Months minus(int months) {
        return Months.months(MathUtils.safeSubtract(this.months, months));
    }

    /**
     * Returns a new instance with the specified number of months taken away.
     * <p>
     * This instance is immutable and unaffected by this method call.
     * 
     * @param months  the amount of months to take away, may be negative, not null
     * @return the new period minus the specified number of months
     * @throws NullPointerException if the months to add is null
     * @throws ArithmeticException if the result overflows an int
     */
    public Months minus(Months months) {
        return Months.months(MathUtils.safeSubtract(this.months, months.months));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a new instance with the months multiplied by the specified scalar.
     * <p>
     * This instance is immutable and unaffected by this method call.
     * 
     * @param scalar  the amount to multiply by, may be negative
     * @return the new period multiplied by the specified scalar
     * @throws ArithmeticException if the result overflows an int
     */
    public Months multipliedBy(int scalar) {
        return Months.months(MathUtils.safeMultiply(months, scalar));
    }

    /**
     * Returns a new instance with the months divided by the specified divisor.
     * The calculation uses integer division, thus 3 divided by 2 is 1.
     * <p>
     * This instance is immutable and unaffected by this method call.
     * 
     * @param divisor  the amount to divide by, may be negative
     * @return the new period divided by the specified divisor
     * @throws ArithmeticException if the divisor is zero
     */
    public Months dividedBy(int divisor) {
        if (divisor == 1) {
            return this;
        }
        return Months.months(months / divisor);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a new instance with the months value negated.
     * 
     * @return the new period with a negated value
     * @throws ArithmeticException if the result overflows an int
     */
    public Months negated() {
        return Months.months(MathUtils.safeNegate(months));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a string representation of the number of months.
     * This will be in the format 'PnM' where n is the number of months.
     * 
     * @return the number of months in ISO8601 string format
     */
    public String toString() {
        return "P" + months + "M";
    }

}
