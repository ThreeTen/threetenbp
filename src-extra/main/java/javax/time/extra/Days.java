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

import static javax.time.calendrical.LocalPeriodUnit.DAYS;

import java.io.Serializable;

import javax.time.calendrical.PeriodUnit;

/**
 * A period representing a number of days.
 * <p>
 * Days is an immutable period that can only store days.
 * It is a type-safe way of representing a number of days in an application.
 * <p>
 * Static factory methods allow you to construct instances.
 * The number of days may be queried using getDays().
 * Basic mathematical operations are provided - plus(), minus(), multipliedBy(),
 * dividedBy() and negated(), all of which return a new instance
 * 
 * <h4>Implementation notes</h4>
 * This class is immutable and thread-safe.
 */
public final class Days extends AbstractPeriodField implements Comparable<Days>, Serializable {

    /**
     * A constant for zero days.
     */
    public static final Days ZERO = new Days(0);

    /**
     * A serialization identifier for this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The number of days in the period.
     */
    private final int days;

    /**
     * Obtains an instance of <code>Days</code>.
     *
     * @param days  the number of days the instance will represent
     * @return the created Days
     */
    public static Days of(int days) {
        if (days == 0) {
            return ZERO;
        }
        return new Days(days);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance using a specific number of days.
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
        return Days.of(days);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the number of days held in this period.
     *
     * @return the number of days
     */
    @Override
    public int getAmount() {
        return days;
    }

    /**
     * Returns a new instance of the subclass with a different number of days.
     *
     * @param amount  the number of days to set in the new instance, may be negative
     * @return a new period element, never null
     */
    @Override
    public Days withAmount(int amount) {
        return Days.of(amount);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the unit defining the amount of time.
     *
     * @return the days unit, never null
     */
    @Override
    public PeriodUnit getUnit() {
        return DAYS;
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
    @Override
    public Days plus(int days) {
        return (Days) super.plus(days);
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
        return plus(days.getAmount());
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
    @Override
    public Days minus(int days) {
        return (Days) super.minus(days);
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
        return minus(days.getAmount());
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
    @Override
    public Days multipliedBy(int scalar) {
        return (Days) super.multipliedBy(scalar);
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
    @Override
    public Days dividedBy(int divisor) {
        return (Days) super.dividedBy(divisor);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a new instance with the days value negated.
     *
     * @return the new period with a negated value
     * @throws ArithmeticException if the result overflows an int
     */
    @Override
    public Days negated() {
        return (Days) super.negated();
    }

    //-----------------------------------------------------------------------
    /**
     * Compares the number of days in this instance to another instance.
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
     * Is the number of days in this instance greater than that in
     * another instance.
     *
     * @param otherDays  the other number of days, not null
     * @return true if this number of days is greater
     * @throws NullPointerException if otherDays is null
     */
    public boolean isGreaterThan(Days otherDays) {
        return compareTo(otherDays) > 0;
    }

    /**
     * Is the number of days in this instance less than that in
     * another instance.
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
     * Returns a string representation of the number of days.
     * This will be in the format 'PnD' where n is the number of days.
     *
     * @return the number of days in ISO8601 string format
     */
    @Override
    public String toString() {
        return "P" + days + "D";
    }

}
