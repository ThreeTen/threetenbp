/*
 * Copyright (c) 2007-2012, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time.extra;

import static javax.time.calendrical.LocalPeriodUnit.YEARS;

import java.io.Serializable;

import javax.time.calendrical.PeriodUnit;

/**
 * A period representing a number of years.
 * <p>
 * Years is an immutable period that can only store years.
 * It is a type-safe way of representing a number of years in an application.
 * <p>
 * Static factory methods allow you to construct instances.
 * The number of years may be queried using getYears().
 * Basic mathematical operations are provided - plus(), minus(), multipliedBy(),
 * dividedBy() and negated(), all of which return a new instance
 * 
 * <h4>Implementation notes</h4>
 * This class is immutable and thread-safe.
 */
public final class Years extends AbstractPeriodField implements Comparable<Years>, Serializable {

    /**
     * A constant for zero years.
     */
    public static final Years ZERO = new Years(0);

    /**
     * A serialization identifier for this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The number of years in the period.
     */
    private final int years;

    /**
     * Obtains an instance of <code>Years</code>.
     *
     * @param years  the number of years the instance will represent
     * @return the created Years
     */
    public static Years of(int years) {
        if (years == 0) {
            return ZERO;
        }
        return new Years(years);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance using a specific number of years.
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
        return Years.of(years);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the number of years held in this period.
     *
     * @return the number of years
     */
    @Override
    public int getAmount() {
        return years;
    }

    /**
     * Returns a new instance of the subclass with a different number of years.
     *
     * @param amount  the number of years to set in the new instance, may be negative
     * @return a new period element, never null
     */
    @Override
    public Years withAmount(int amount) {
        return Years.of(amount);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the unit defining the amount of time.
     *
     * @return the years unit, never null
     */
    @Override
    public PeriodUnit getUnit() {
        return YEARS;
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
    @Override
    public Years plus(int years) {
        return (Years) super.plus(years);
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
        return plus(years.getAmount());
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
    @Override
    public Years minus(int years) {
        return (Years) super.minus(years);
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
        return minus(years.getAmount());
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
    @Override
    public Years multipliedBy(int scalar) {
        return (Years) super.multipliedBy(scalar);
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
    @Override
    public Years dividedBy(int divisor) {
        return (Years) super.dividedBy(divisor);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a new instance with the years value negated.
     *
     * @return the new period with a negated value
     * @throws ArithmeticException if the result overflows an int
     */
    @Override
    public Years negated() {
        return (Years) super.negated();
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
     * Returns a string representation of the number of years.
     * This will be in the format 'PnY' where n is the number of years.
     *
     * @return the number of years in ISO8601 string format
     */
    @Override
    public String toString() {
        return "P" + years + "Y";
    }

}
