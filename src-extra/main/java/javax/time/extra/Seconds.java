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

import static javax.time.calendrical.LocalPeriodUnit.SECONDS;

import java.io.Serializable;

import javax.time.calendrical.PeriodUnit;

/**
 * A period representing a number of seconds.
 * <p>
 * Seconds is an immutable period that can only store seconds.
 * It is a type-safe way of representing a number of seconds in an application.
 * <p>
 * Static factory methods allow you to construct instances.
 * The number of seconds may be queried using getSeconds().
 * Basic mathematical operations are provided - plus(), minus(), multipliedBy(),
 * dividedBy() and negated(), all of which return a new instance
 * 
 * <h4>Implementation notes</h4>
 * This class is immutable and thread-safe.
 */
public final class Seconds extends AbstractPeriodField implements Comparable<Seconds>, Serializable {

    /**
     * A constant for zero seconds.
     */
    public static final Seconds ZERO = new Seconds(0);

    /**
     * A serialization identifier for this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The number of seconds in the period.
     */
    private final int seconds;

    /**
     * Obtains an instance of <code>Seconds</code>.
     *
     * @param seconds  the number of seconds the instance will represent
     * @return the created Seconds
     */
    public static Seconds of(int seconds) {
        if (seconds == 0) {
            return ZERO;
        }
        return new Seconds(seconds);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance using a specific number of seconds.
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
        return Seconds.of(seconds);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the number of seconds held in this period.
     *
     * @return the number of seconds
     */
    @Override
    public int getAmount() {
        return seconds;
    }

    /**
     * Returns a new instance of the subclass with a different number of seconds.
     *
     * @param amount  the number of seconds to set in the new instance, may be negative
     * @return a new period element, never null
     */
    @Override
    public Seconds withAmount(int amount) {
        return Seconds.of(amount);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the unit defining the amount of time.
     *
     * @return the seconds unit, never null
     */
    @Override
    public PeriodUnit getUnit() {
        return SECONDS;
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
    @Override
    public Seconds plus(int seconds) {
        return (Seconds) super.plus(seconds);
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
        return plus(seconds.getAmount());
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
    @Override
    public Seconds minus(int seconds) {
        return (Seconds) super.minus(seconds);
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
        return minus(seconds.getAmount());
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
    @Override
    public Seconds multipliedBy(int scalar) {
        return (Seconds) super.multipliedBy(scalar);
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
    @Override
    public Seconds dividedBy(int divisor) {
        return (Seconds) super.dividedBy(divisor);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a new instance with the seconds value negated.
     *
     * @return the new period with a negated value
     * @throws ArithmeticException if the result overflows an int
     */
    @Override
    public Seconds negated() {
        return (Seconds) super.negated();
    }

    //-----------------------------------------------------------------------
    /**
     * Compares the number of seconds in this instance to another instance.
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
     * Is the number of seconds in this instance greater than that in
     * another instance.
     *
     * @param otherSeconds  the other number of seconds, not null
     * @return true if this number of seconds is greater
     * @throws NullPointerException if otherSeconds is null
     */
    public boolean isGreaterThan(Seconds otherSeconds) {
        return compareTo(otherSeconds) > 0;
    }

    /**
     * Is the number of seconds in this instance less than that in
     * another instance.
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
     * Returns a string representation of the number of seconds.
     * This will be in the format 'PTnS' where n is the number of seconds.
     *
     * @return the number of seconds in ISO8601 string format
     */
    @Override
    public String toString() {
        return "PT" + seconds + "S";
    }

}
