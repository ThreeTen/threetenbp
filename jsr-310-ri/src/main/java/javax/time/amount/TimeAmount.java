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
package javax.time.amount;

import java.io.Serializable;

import javax.math.MathUtils;
import javax.time.Period;
import javax.time.part.TimePart;

/**
 * An amount of time measured using a single time part.
 * <p>
 * A <code>TimeAmount</code> can be used to store an amount of time measured
 * in a human scale time part, such as years, months, days, hours, minutes or
 * seconds, such as 34 hours. Implementations cannot be used to store a
 * combination of parts, such as 34 hours and 5 seconds.
 * <p>
 * TimeAmount is an abstract class and must be subclassed with care to ensure
 * other classes in the framework operate correctly.
 * All subclasses must be final, immutable and thread-safe.
 *
 * @param <U> the time part that the amount is measured in
 * @author Stephen Colebourne
 */
public abstract class TimeAmount<U extends TimePart>
        implements Period, Comparable<TimeAmount<U>>, Serializable {

    /**
     * The amount of time being represented, may be negative.
     */
    private final int amount;
    /**
     * The type being represented, not null.
     */
    private final TimeAmountType<U> type;

    /**
     * Constructor.
     *
     * @param <U> the time part that the amount is measured in
     * @param amount  the amount of time to represent, may be negative
     * @param type  the type of time to represent, not null
     * @return the created time amount
     */
    public static <U extends TimePart> TimeAmount<U> timeAmount(int amount, TimeAmountType<U> type) {
        return new Impl<U>(amount, type);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor.
     *
     * @param amount  the amount of time to represent, may be negative
     * @param type  the type of time to represent, not null
     */
    protected TimeAmount(int amount, TimeAmountType<U> type) {
        super();
        this.amount = amount;
        this.type = type;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the amount of time, possibly negative, represented by this object.
     *
     * @return the time amount value, may be negative
     */
    public int getValue() {
        return amount;
    }

    /**
     * Returns a new instance, of the same type, with the specified value.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param newValue  the new value
     * @return a new instance with the same type and new value, never null
     */
    public abstract TimeAmount<U> withValue(int newValue);

    //-----------------------------------------------------------------------
    /**
     * Gets the type representing the time part stored in this object.
     *
     * @return the time amount type, never null
     */
    public TimeAmountType<U> getType() {
        return type;
    }

    /**
     * Converts the amount of time to a different unit.
     *
     * @param <T>  the time part of the amount to convert to
     * @param type  the type of the amount to convert to, not null
     * @return a new instance with the time amount negated, never null
     * @throws ArithmeticException if the result overflows an int
     * @throws UnsupportedOperationException if the conversion is not supported
     */
    public <T extends TimePart> TimeAmount<T> convertTo(TimeAmountType<T> type) {
        return getType().convert(getValue(), type);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the time amount for the specified part.
     *
     * @param <T>  the time part type that is required
     * @param type  the time part type that is required, not null
     * @return this if the type matches, else null
     */
    @SuppressWarnings("unchecked")
    public <T extends TimePart> TimeAmount<T> getAmount(T type) {
        if (type != getType().getPart()) {
            return null;
        }
        return (TimeAmount<T>) this;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a new instance with the specified time amount added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param amount  the time amount to add, may be negative
     * @return a new instance with the specified time amount added
     * @throws ArithmeticException if the result overflows an int
     */
    public TimeAmount<U> plus(int amount) {
        if (amount == 0) {
            return this;
        }
        return withValue(MathUtils.safeAdd(this.getValue(), amount));
    }

    /**
     * Returns a new instance with the specified time amount added.
     * <p>
     * You can only add time amounts with the same type.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param amount  the time amount to add, may be negative, not null
     * @return a new instance with the specified time amount added
     * @throws NullPointerException if the days to add is null
     * @throws ArithmeticException if the result overflows an int
     */
    public TimeAmount<U> plus(TimeAmount<U> amount) {
        checkMatchingType(amount);
        return plus(amount.getValue());
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a new instance with the specified time amount taken away.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param amount  the time amount to take away, may be negative
     * @return a new instance with the specified time amount subtracted
     * @throws ArithmeticException if the result overflows an int
     */
    @SuppressWarnings("unchecked")
    public TimeAmount<U> minus(int amount) {
        if (amount == 0) {
            return this;
        }
        return withValue(MathUtils.safeSubtract(this.getValue(), amount));
    }

    /**
     * Returns a new instance with the specified time amount taken away.
     * <p>
     * You can only subtract time amounts with the same type.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param amount  the time amount to take away, may be negative, not null
     * @return a new instance with the specified time amount subtracted
     * @throws NullPointerException if the amount to add is null
     * @throws ArithmeticException if the result overflows an int
     */
    public TimeAmount<U> minus(TimeAmount<U> amount) {
        checkMatchingType(amount);
        return minus(amount.getValue());
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a new instance with the amount multiplied by the specified scalar.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param scalar  the amount to multiply by, may be negative
     * @return a new instance with the multiplied time amount
     * @throws ArithmeticException if the result overflows an int
     */
    public TimeAmount<U> multipliedBy(int scalar) {
        return withValue(MathUtils.safeMultiply(getValue(), scalar));
    }

    /**
     * Returns a new instance with the amount divided by the specified divisor.
     * The calculation uses integer division, thus 3 divided by 2 is 1.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param divisor  the amount to divide by, may be negative
     * @return a new instance with the divided time amount
     * @throws ArithmeticException if the divisor is zero
     */
    @SuppressWarnings("unchecked")
    public TimeAmount<U> dividedBy(int divisor) {
        if (divisor == 1) {
            return this;
        }
        return withValue(getValue() / divisor);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a new instance with the amount negated.
     *
     * @return a new instance with the time amount negated
     * @throws ArithmeticException if the result overflows an int
     */
    public TimeAmount<U> negated() {
        return withValue(MathUtils.safeNegate(getValue()));
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this TimeAmount to another based on the amount of time.
     * <p>
     * You can only compare time amounts with the same type.
     *
     * @param other  the other time amount to compare to, not null
     * @return the comparator result, negative if less, postive if greater, zero if equal
     * @throws ClassCastException if the type is different
     * @throws NullPointerException if other is null
     */
    public int compareTo(TimeAmount<U> other) {
        checkMatchingType(other);
        return MathUtils.safeCompare(this.getValue(), other.getValue());
    }

    /**
     * Is the time amount in this instance greater than that in another instance.
     *
     * @param other  the other time amount, not null
     * @return true if this time amount is greater
     * @throws NullPointerException if other is null
     */
    public boolean isGreaterThan(TimeAmount<U> other) {
        return compareTo(other) > 0;
    }

    /**
     * Is the time amount in this instance less than that in another instance.
     *
     * @param other  the other time amount, not null
     * @return true if this time amount is less
     * @throws NullPointerException if other is null
     */
    public boolean isLessThan(TimeAmount<U> other) {
        return compareTo(other) < 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Is this instance equal to that specified, evaluating the time amount.
     *
     * @param other  the other time amount, null returns false
     * @return true if this time amount is the same as that specified
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object other) {
        if (this == other) {
           return true;
        }
        if (other instanceof TimeAmount) {
            TimeAmount amount = (TimeAmount) other;
            return this.getType() == amount.getType() &&
                    this.getValue() == amount.getValue();
        }
        return false;
    }

    /**
     * A hash code for this time amount.
     *
     * @return a suitable hash code
     */
    @Override
    public int hashCode() {
        return getValue() * getType().hashCode();
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a string representation of the time amount.
     *
     * @return a description of the amount of time
     */
    @Override
    public String toString() {
        return getType().getName() + "s=" + getValue();
    }

    //-----------------------------------------------------------------------
    /**
     * Handles generics erasure by confirming that the types really do match.
     *
     * @param other  the other time amount which should be the same type
     */
    private void checkMatchingType(TimeAmount<U> other) {
        if (this.getType() != other.getType()) {
            throw new ClassCastException("TimeAmountType differs due to using a generics raw type: " +
                    this.getType().getName() + " != " + other.getType().getName());
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Generic time amount that can be used to store an amount of a single time part.
     * <p>
     * For example, this class can be used to store 34 hours, or to store 5 seconds.
     * This class cannot be used to store the combination 34 hours and 5 seconds.
     *
     * @param <U> the time part that the amount is measured in
     * @author Stephen Colebourne
     */
    private static final class Impl<U extends TimePart>
            extends TimeAmount<U> {

        /**
         * A serialization identifier for this instance.
         */
        private static final long serialVersionUID = 1903627244616272386L;

        /**
         * Constructor.
         *
         * @param amount  the amount of time to represent, may be negative
         * @param type  the type of time to represent, not null
         */
        public Impl(int amount, TimeAmountType<U> type) {
            super(amount, type);
        }

        //-----------------------------------------------------------------------
        /** {@inheritDoc} */
        @Override
        public Impl<U> withValue(int newValue) {
            return new Impl<U>(newValue, getType());
        }
    }

}
