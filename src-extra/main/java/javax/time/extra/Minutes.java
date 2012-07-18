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

import static javax.time.calendrical.LocalPeriodUnit.MINUTES;

import java.io.Serializable;

import javax.time.calendrical.PeriodUnit;

/**
 * A period representing a number of minutes.
 * <p>
 * Minutes is an immutable period that can only store minutes.
 * It is a type-safe way of representing a number of minutes in an application.
 * <p>
 * Static factory methods allow you to construct instances.
 * The number of minutes may be queried using getMinutes().
 * Basic mathematical operations are provided - plus(), minus(), multipliedBy(),
 * dividedBy() and negated(), all of which return a new instance
 * 
 * <h4>Implementation notes</h4>
 * This class is immutable and thread-safe.
 */
public final class Minutes extends AbstractPeriodField implements Comparable<Minutes>, Serializable {

    /**
     * A constant for zero minutes.
     */
    public static final Minutes ZERO = new Minutes(0);

    /**
     * A serialization identifier for this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The number of minutes in the period.
     */
    private final int minutes;

    /**
     * Obtains an instance of <code>Minutes</code>.
     *
     * @param minutes  the number of minutes the instance will represent
     * @return the created Minutes
     */
    public static Minutes of(int minutes) {
        if (minutes == 0) {
            return ZERO;
        }
        return new Minutes(minutes);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance using a specific number of minutes.
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
        return Minutes.of(minutes);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the number of minutes held in this period.
     *
     * @return the number of minutes
     */
    @Override
    public int getAmount() {
        return minutes;
    }

    /**
     * Returns a new instance of the subclass with a different number of minutes.
     *
     * @param amount  the number of minutes to set in the new instance, may be negative
     * @return a new period element, never null
     */
    @Override
    public Minutes withAmount(int amount) {
        return Minutes.of(amount);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the unit defining the amount of time.
     *
     * @return the minutes unit, never null
     */
    @Override
    public PeriodUnit getUnit() {
        return MINUTES;
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
    @Override
    public Minutes plus(int minutes) {
        return (Minutes) super.plus(minutes);
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
        return plus(minutes.getAmount());
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
    @Override
    public Minutes minus(int minutes) {
        return (Minutes) super.minus(minutes);
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
        return minus(minutes.getAmount());
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
    @Override
    public Minutes multipliedBy(int scalar) {
        return (Minutes) super.multipliedBy(scalar);
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
    @Override
    public Minutes dividedBy(int divisor) {
        return (Minutes) super.dividedBy(divisor);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a new instance with the minutes value negated.
     *
     * @return the new period with a negated value
     * @throws ArithmeticException if the result overflows an int
     */
    @Override
    public Minutes negated() {
        return (Minutes) super.negated();
    }

    //-----------------------------------------------------------------------
    /**
     * Compares the number of minutes in this instance to another instance.
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
     * Is the number of minutes in this instance greater than that in
     * another instance.
     *
     * @param otherMinutes  the other number of minutes, not null
     * @return true if this number of minutes is greater
     * @throws NullPointerException if otherMinutes is null
     */
    public boolean isGreaterThan(Minutes otherMinutes) {
        return compareTo(otherMinutes) > 0;
    }

    /**
     * Is the number of minutes in this instance less than that in
     * another instance.
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
     * Returns a string representation of the number of minutes.
     * This will be in the format 'PTnM' where n is the number of minutes.
     *
     * @return the number of minutes in ISO8601 string format
     */
    @Override
    public String toString() {
        return "PT" + minutes + "M";
    }

}
