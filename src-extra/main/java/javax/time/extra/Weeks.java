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

import static javax.time.calendrical.LocalPeriodUnit.WEEKS;

import java.io.Serializable;

import javax.time.calendrical.PeriodUnit;

/**
 * A period representing a number of weeks.
 * <p>
 * Weeks is an immutable period that can only store weeks.
 * It is a type-safe way of representing a number of weeks in an application.
 * <p>
 * Static factory methods allow you to construct instances.
 * The number of weeks may be queried using getWeeks().
 * Basic mathematical operations are provided - plus(), minus(), multipliedBy(),
 * dividedBy() and negated(), all of which return a new instance
 * 
 * <h4>Implementation notes</h4>
 * This class is immutable and thread-safe.
 */
public final class Weeks extends AbstractPeriodField implements Comparable<Weeks>, Serializable {

    /**
     * A constant for zero weeks.
     */
    public static final Weeks ZERO = new Weeks(0);

    /**
     * A serialization identifier for this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The number of weeks in the period.
     */
    private final int weeks;

    /**
     * Obtains an instance of <code>Weeks</code>.
     *
     * @param weeks  the number of weeks the instance will represent
     * @return the created Weeks
     */
    public static Weeks of(int weeks) {
        if (weeks == 0) {
            return ZERO;
        }
        return new Weeks(weeks);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance using a specific number of weeks.
     *
     * @param weeks  the weeks to use
     */
    private Weeks(int weeks) {
        super();
        this.weeks = weeks;
    }

    /**
     * Resolves singletons.
     *
     * @return the singleton instance
     */
    private Object readResolve() {
        return Weeks.of(weeks);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the number of weeks held in this period.
     *
     * @return the number of weeks
     */
    @Override
    public int getAmount() {
        return weeks;
    }

    /**
     * Returns a new instance of the subclass with a different number of weeks.
     *
     * @param amount  the number of weeks to set in the new instance, may be negative
     * @return a new period element, never null
     */
    @Override
    public Weeks withAmount(int amount) {
        return Weeks.of(amount);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the unit defining the amount of time.
     *
     * @return the weeks unit, never null
     */
    @Override
    public PeriodUnit getUnit() {
        return WEEKS;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a new instance with the specified number of weeks added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param weeks  the amount of weeks to add, may be negative
     * @return the new period plus the specified number of weeks
     * @throws ArithmeticException if the result overflows an int
     */
    @Override
    public Weeks plus(int weeks) {
        return (Weeks) super.plus(weeks);
    }

    /**
     * Returns a new instance with the specified number of weeks added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param weeks  the amount of weeks to add, may be negative, not null
     * @return the new period plus the specified number of weeks
     * @throws NullPointerException if the weeks to add is null
     * @throws ArithmeticException if the result overflows an int
     */
    public Weeks plus(Weeks weeks) {
        return plus(weeks.getAmount());
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a new instance with the specified number of weeks taken away.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param weeks  the amount of weeks to take away, may be negative
     * @return the new period minus the specified number of weeks
     * @throws ArithmeticException if the result overflows an int
     */
    @Override
    public Weeks minus(int weeks) {
        return (Weeks) super.minus(weeks);
    }

    /**
     * Returns a new instance with the specified number of weeks taken away.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param weeks  the amount of weeks to take away, may be negative, not null
     * @return the new period minus the specified number of weeks
     * @throws NullPointerException if the weeks to add is null
     * @throws ArithmeticException if the result overflows an int
     */
    public Weeks minus(Weeks weeks) {
        return minus(weeks.getAmount());
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a new instance with the weeks multiplied by the specified scalar.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param scalar  the amount to multiply by, may be negative
     * @return the new period multiplied by the specified scalar
     * @throws ArithmeticException if the result overflows an int
     */
    @Override
    public Weeks multipliedBy(int scalar) {
        return (Weeks) super.multipliedBy(scalar);
    }

    /**
     * Returns a new instance with the weeks divided by the specified divisor.
     * The calculation uses integer division, thus 3 divided by 2 is 1.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param divisor  the amount to divide by, may be negative
     * @return the new period divided by the specified divisor
     * @throws ArithmeticException if the divisor is zero
     */
    @Override
    public Weeks dividedBy(int divisor) {
        return (Weeks) super.dividedBy(divisor);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a new instance with the weeks value negated.
     *
     * @return the new period with a negated value
     * @throws ArithmeticException if the result overflows an int
     */
    @Override
    public Weeks negated() {
        return (Weeks) super.negated();
    }

    //-----------------------------------------------------------------------
    /**
     * Compares the number of weeks in this instance to another instance.
     *
     * @param otherWeeks  the other number of weeks, not null
     * @return the comparator value, negative if less, postive if greater
     * @throws NullPointerException if otherWeeks is null
     */
    public int compareTo(Weeks otherWeeks) {
        int thisValue = this.weeks;
        int otherValue = otherWeeks.weeks;
        return (thisValue < otherValue ? -1 : (thisValue == otherValue ? 0 : 1));
    }

    /**
     * Is the number of weeks in this instance greater than that in
     * another instance.
     *
     * @param otherWeeks  the other number of weeks, not null
     * @return true if this number of weeks is greater
     * @throws NullPointerException if otherWeeks is null
     */
    public boolean isGreaterThan(Weeks otherWeeks) {
        return compareTo(otherWeeks) > 0;
    }

    /**
     * Is the number of weeks in this instance less than that in
     * another instance.
     *
     * @param otherWeeks  the other number of weeks, not null
     * @return true if this number of weeks is less
     * @throws NullPointerException if otherWeeks is null
     */
    public boolean isLessThan(Weeks otherWeeks) {
        return compareTo(otherWeeks) < 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a string representation of the number of weeks.
     * This will be in the format 'PnW' where n is the number of weeks.
     *
     * @return the number of weeks in ISO8601 string format
     */
    @Override
    public String toString() {
        return "P" + weeks + "W";
    }

}
