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
package javax.quantity;

import java.io.Serializable;

/**
 * A quantity consisting of an amount of a specified unit.
 *
 * @param <A> the numeric type holding the amount
 * @param <U> the unit that the quantity is measured in
 * @author Stephen Colebourne
 */
public abstract class Quantity<A extends Number, U extends Unit> implements Serializable {

    /**
     * Serialization version id.
     */
    private static final long serialVersionUID = -1247656583871L;

    //-----------------------------------------------------------------------
    /**
     * Gets the amount of the quantity.
     *
     * @return the amount of the quantity, never null
     */
    public abstract A amount();

    /**
     * Gets the unit of the quantity.
     *
     * @return the unit of the quantity, never null
     */
    public abstract U unit();

    /**
     * Compares this quantity to another based on the value and unit.
     *
     * @param other  the other quantity to compare to, null returns false
     * @return true if equal
     */
    @Override
    public boolean equals(Object other) {
        if (other instanceof Quantity) {
            Quantity<?, ?> quantity = (Quantity<?, ?>) other;
            return amount().equals(quantity.amount()) &&
                    unit().equals(quantity.unit());
        }
//        Quantity<Integer, DurationScale> q0 = null;        // JSR-275
//        Quantity<Integer, SecondUnit, DurationScale> qa = null;     // generics
//        Quantity<Integer, Unit<DurationScale>> q1 = null;  // adjusted
//        Quantity<Integer, DurationUnit> q2 = null;         // subclass
//        Quantity<Integer, SecondUnit> q3 = null;           // subclass
//        Quantity<Integer, MinuteUnit> qx = convert(q3, new MinuteUnit());
        return false;
    }

//    public <A extends Number, S extends UnitScale, T extends Unit<S>>
//            Quantity<A, T> convert(Quantity<A, Unit<S>> in, T unit) {
//        return null;
//    }
//    public <A extends Number, S extends Unit>
//            Quantity<A, Unit<S>> convert2(Quantity<A, S> in, S unit) {
//        return null;
//    }

    /**
     * A suitable hash code that combines the value and unit.
     *
     * @return a suitable hash code
     */
    @Override
    public int hashCode() {
        return amount().hashCode() + 3 * unit().hashCode();
    }

    /**
     * A string representation of the quantity.
     *
     * @return a standard string representation, never null
     */
    @Override
    public String toString() {
        return amount().toString() + " " + unit().toString();
    }

//    //-----------------------------------------------------------------------
//    /**
//     * Gets the amount of the quantity as a <code>int</code>.
//     * This may be rounded or truncated.
//     *
//     * @return the amount of the quantity
//     */
//    public int intValue() {
//        return value().intValue();
//    }
//
//    /**
//     * Gets the amount of the quantity as a <code>long</code>.
//     * This may be rounded or truncated.
//     *
//     * @return the amount of the quantity
//     */
//    public long longValue() {
//        return value().longValue();
//    }
//
//    /**
//     * Gets the amount of the quantity as a <code>float</code>.
//     * This may be rounded or truncated.
//     *
//     * @return the amount of the quantity
//     */
//    public float floatValue() {
//        return value().floatValue();
//    }
//
//    /**
//     * Gets the amount of the quantity as a <code>double</code>.
//     * This may be rounded or truncated.
//     *
//     * @return the amount of the quantity
//     */
//    public double doubleValue() {
//        return value().doubleValue();
//    }

}
