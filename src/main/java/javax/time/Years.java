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
 * A time period representing a number of years.
 * <p>
 * Years is an immutable period that can only store years.
 * It is a type-safe way of representing a number of years in an application.
 * <p>
 * Static factory methods allow you to constuct instances.
 * The number of years may be queried using getYears().
 * Basic mathematical operations are provided - plus(), minus(), multipliedBy(),
 * dividedBy() and negated(), all of which return a new instance
 * <p>
 * Years is thread-safe and immutable. 
 * 
 * @author Stephen Colebourne
 */
public final class Years implements Comparable<Years> {

    /**
     * A constant for zero years.
     */
    private static final Years ZERO = new Years(0);

    /**
     * The number of years in the period.
     */
    private final int years;

    /**
     * Obtains an instance of <code>Years</code>.
     * 
     * @param years  the number of years the instance will represent
     */
    public static Years years(int years) {
        if (years == 0) {
            return ZERO;
        }
        return new Years(years);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance using a specific numbr of years.
     * 
     * @param years  the years to use
     */
    private Years(int years) {
        super();
        this.years = years;
    }

    /**
     * Resolves singletons.
     * 
     * @return the singleton instance
     */
    private Object readResolve() {
        return Years.years(years);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the number of years held in this period.
     * 
     * @return the number of years
     */
    public int getYears() {
        return years;
    }

    //-----------------------------------------------------------------------
    /**
     * Compares the number of years in this instance to another instance.
     * 
     * @param otherYears  the other number of years, not null
     * @return the comparator value, negative if less, postive if greater
     * @throws NullPointerException if otherYears is null
     */
    public int compareTo(Years otherYears) {
        int thisValue = this.years;
        int otherValue = otherYears.years;
        return (thisValue < otherValue ? -1 : (thisValue == otherValue ? 0 : 1));
    }

    /**
     * Is the number of years in this instance greater than that in
     * another instance.
     * 
     * @param otherYears  the other number of years, not null
     * @return true if this number of years is greater
     * @throws NullPointerException if otherYears is null
     */
    public boolean isGreaterThan(Years otherYears) {
        return compareTo(otherYears) > 0;
    }

    /**
     * Is the number of years in this instance less than that in
     * another instance.
     * 
     * @param otherYears  the other number of years, not null
     * @return true if this number of years is less
     * @throws NullPointerException if otherYears is null
     */
    public boolean isLessThan(Years otherYears) {
        return compareTo(otherYears) < 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Is this instance equal to that specified, evaluating the number of years.
     * 
     * @param otherYears  the other number of years, null returns false
     * @return true if this number of years is the same as that specified
     */
    public boolean equals(Object otherYears) {
        if (otherYears instanceof Years) {
            return years == ((Years) otherYears).years;
        }
        return false;
    }

    /**
     * A hashcode for the years object.
     * 
     * @return a suitable hashcode
     */
    public int hashCode() {
        return years;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a new instance with the specified number of years added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     * 
     * @param years  the amount of years to add, may be negative
     * @return the new period plus the specified number of years
     * @throws ArithmeticException if the result overflows an int
     */
    public Years plus(int years) {
        if (years == 0) {
            return this;
        }
        return Years.years(MathUtils.safeAdd(this.years, years));
    }

    /**
     * Returns a new instance with the specified number of years added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     * 
     * @param years  the amount of years to add, may be negative, not null
     * @return the new period plus the specified number of years
     * @throws NullPointerException if the years to add is null
     * @throws ArithmeticException if the result overflows an int
     */
    public Years plus(Years years) {
        return Years.years(MathUtils.safeAdd(this.years, years.years));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a new instance with the specified number of years taken away.
     * <p>
     * This instance is immutable and unaffected by this method call.
     * 
     * @param years  the amount of years to take away, may be negative
     * @return the new period minus the specified number of years
     * @throws ArithmeticException if the result overflows an int
     */
    public Years minus(int years) {
        return Years.years(MathUtils.safeSubtract(this.years, years));
    }

    /**
     * Returns a new instance with the specified number of years taken away.
     * <p>
     * This instance is immutable and unaffected by this method call.
     * 
     * @param years  the amount of years to take away, may be negative, not null
     * @return the new period minus the specified number of years
     * @throws NullPointerException if the years to add is null
     * @throws ArithmeticException if the result overflows an int
     */
    public Years minus(Years years) {
        return Years.years(MathUtils.safeSubtract(this.years, years.years));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a new instance with the years multiplied by the specified scalar.
     * <p>
     * This instance is immutable and unaffected by this method call.
     * 
     * @param scalar  the amount to multiply by, may be negative
     * @return the new period multiplied by the specified scalar
     * @throws ArithmeticException if the result overflows an int
     */
    public Years multipliedBy(int scalar) {
        return Years.years(MathUtils.safeMultiply(years, scalar));
    }

    /**
     * Returns a new instance with the years divided by the specified divisor.
     * The calculation uses integer division, thus 3 divided by 2 is 1.
     * <p>
     * This instance is immutable and unaffected by this method call.
     * 
     * @param divisor  the amount to divide by, may be negative
     * @return the new period divided by the specified divisor
     * @throws ArithmeticException if the divisor is zero
     */
    public Years dividedBy(int divisor) {
        if (divisor == 1) {
            return this;
        }
        return Years.years(years / divisor);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a new instance with the years value negated.
     * 
     * @return the new period with a negated value
     * @throws ArithmeticException if the result overflows an int
     */
    public Years negated() {
        return Years.years(MathUtils.safeNegate(years));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a string representation of the number of years.
     * This will be in the format 'PnD' where n is the number of years.
     * 
     * @return the number of years in ISO8601 string format
     */
    public String toString() {
        return "P" + years + "Y";
    }

}
