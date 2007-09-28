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
package javax.time.duration;

import javax.time.MathUtils;

/**
 * A duration measured in terms of a single field, such as days or seconds.
 * <p>
 * Years is an immutable period that can only store years.
 * It is a type-safe way of representing a number of years in an application.
 * <p>
 * Static factory methods allow you to constuct instances.
 * The number of years may be queried using getYears().
 * Basic mathematical operations are provided - plus(), minus(), multipliedBy(),
 * dividedBy() and negated(), all of which return a new instance
 * <p>
 * DurationField is an abstract class and must be implemented with care to ensure
 * other classes in the framework operate correctly.
 * All instantiable subclasses must be final, immutable and thread-safe.
 *
 * @author Stephen Colebourne
 */
public abstract class DurationField implements Durational {

    //-----------------------------------------------------------------------
    /**
     * Constructs a new instance.
     */
    protected DurationField() {
        super();
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the amount of time in this duration field.
     *
     * @return the amount of time of this duration field
     */
    public abstract int getAmount();

    /**
     * Returns a new instance of the subclass with a different amount of time.
     *
     * @param amount  the amount of time to set in the new duration field, may be negative
     * @return a new duration field, never null
     */
    public abstract DurationField withAmount(int amount);

    //-----------------------------------------------------------------------
    /**
     * Returns a new instance with the specified amount of time added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param amount  the amount of time to add, may be negative
     * @return the new duration field plus the specified amount of time, never null
     * @throws ArithmeticException if the result overflows an int
     */
    public DurationField plus(int amount) {
        if (amount == 0) {
            return this;
        }
        return withAmount(MathUtils.safeAdd(getAmount(), amount));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a new instance with the specified amount of time taken away.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param amount  the amount of time to take away, may be negative
     * @return the new period minus the specified amount of time, never null
     * @throws ArithmeticException if the result overflows an int
     */
    public DurationField minus(int amount) {
        return withAmount(MathUtils.safeSubtract(getAmount(), amount));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a new instance with the amount multiplied by the specified scalar.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param scalar  the amount to multiply by, may be negative
     * @return the new period multiplied by the specified scalar, never null
     * @throws ArithmeticException if the result overflows an int
     */
    public DurationField multipliedBy(int scalar) {
        return withAmount(MathUtils.safeMultiply(getAmount(), scalar));
    }

    /**
     * Returns a new instance with the amount divided by the specified divisor.
     * The calculation uses integer division, thus 3 divided by 2 is 1.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param divisor  the amount to divide by, may be negative
     * @return the new period divided by the specified divisor, never null
     * @throws ArithmeticException if the divisor is zero
     */
    public DurationField dividedBy(int divisor) {
        if (divisor == 1) {
            return this;
        }
        return withAmount(getAmount() / divisor);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a new instance with the amount negated.
     *
     * @return the new period with a negated amount, never null
     * @throws ArithmeticException if the result overflows an int
     */
    public DurationField negated() {
        return withAmount(MathUtils.safeNegate(getAmount()));
    }

    //-----------------------------------------------------------------------
    /**
     * Is this instance equal to that specified, evaluating the amount of time.
     *
     * @param other  the other amount of time, null returns false
     * @return true if this amount of time is the same as that specified
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
           return true;
        }
        if (other != null && other.getClass() == this.getClass()) {
            return getAmount() == ((DurationField) other).getAmount();
        }
        return false;
    }

    /**
     * A hashcode for the duration field object.
     *
     * @return a suitable hashcode
     */
    @Override
    public int hashCode() {
        return getAmount();
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a string representation of the amount of time.
     *
     * @return the amount of time in ISO8601 string format
     */
    @Override
    public abstract String toString();

}
