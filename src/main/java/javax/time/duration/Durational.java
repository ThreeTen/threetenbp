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

import java.util.Map;

/**
 * Interface implemented by all objects that can provide durational information.
 * <p>
 * Durational is the interface that is shared amongst many other classes in
 * the Java Time Framework. Many low level APIs are defined to accept a
 * Durational, however it is less common that you will hold instances directly.
 * <p>
 * Durational is an interface and must be implemented with care to ensure
 * other classes in the framework operate correctly.
 * All instantiable subclasses must be final, immutable and thread-safe.
 *
 * @author Stephen Colebourne
 */
public interface Durational {

    /**
     * Checks whether a given unit is supported.
     * <p>
     * Not all implementations of <code>Durational</code> support storage of
     * all duration units. This method allows you to determine which units
     * are supported by this instance.
     *
     * @param unit  the unit to check for, null returns false
     * @return whether the unit is supported
     */
    boolean isSupported(DurationUnit unit) ;

    /**
     * Gets the map of duration unit to amount which defines the duration.
     * The map iterators are sorted by duration unit, returning the largest first.
     * <p>
     * Implementations must ensure that the map never contains null.
     * Implementations should return an unmodifiable map from this method.
     *
     * @return the map of duration amounts, never null, never contains null
     */
    Map<DurationUnit, Integer> getDurationalMap() ;

//    /**
//     * Gets the amount in the specified unit.
//     *
//     * @param unit  the unit to update, not null
//     * @return the amount in the specified unit, never null
//     * @throws IllegalArgumentException if the unit is unsupported
//     */
//    Integer getAmount(DurationUnit unit) ;
//
////    /**
////     * Returns a new instance with the specified unit updated.
////     * <p>
////     * Implementations must ensure that they return their own type instead of
////     * <code>Durational</code>.
////     *
////     * @param amount  the amount to update to
////     * @param unit  the unit to update, not null
////     * @return the new updated durational instance, never null
////     * @throws IllegalArgumentException if the unit is unsupported
////     */
////    Durational with(Integer amount, DurationUnit unit) ;
//
//    /**
//     * Returns a new instance with the duration updated as per the specified duration.
//     * <p>
//     * Implementations must ensure that they return their own type instead of
//     * <code>Durational</code>.
//     *
//     * @param duration  the duration to update to, not null
//     * @return the new updated durational instance, never null
//     * @throws IllegalArgumentException if any unit is unsupported
//     */
//    Durational with(Durational duration) ;
//
////    /**
////     * Returns a new instance with the specified duration added.
////     * <p>
////     * Implementations must ensure that they return their own type instead of
////     * <code>Durational</code>.
////     *
////     * @param amount  the amount to add
////     * @param unit  the unit to update, not null
////     * @return the new updated durational instance, never null
////     * @throws IllegalArgumentException if the unit is unsupported
////     */
////    Durational plus(Integer amount, DurationUnit unit) ;
//
//    /**
//     * Returns a new instance with the specified duration added.
//     * <p>
//     * Implementations must ensure that they return their own type instead of
//     * <code>Durational</code>.
//     *
//     * @param durationToAdd  the duration to add, not null
//     * @return the new updated durational instance, never null
//     * @throws IllegalArgumentException if any unit is unsupported
//     * @throws ArithmeticException if the calculation result overflows
//     */
//    Durational plus(Durational durationToAdd) ;
//
//    /**
//     * Returns a new instance with the specified duration subtracted.
//     * <p>
//     * Implementations must ensure that they return their own type instead of
//     * <code>Durational</code>.
//     *
//     * @param durationToSubtract  the duration to subtract, not null
//     * @return the new updated durational instance, never null
//     * @throws IllegalArgumentException if any unit is unsupported
//     * @throws ArithmeticException if the calculation result overflows
//     */
//    Durational minus(Durational durationToSubtract) ;
//
//    /**
//     * Returns a new instance with each element in this duration multiplied
//     * by the specified scalar.
//     * <p>
//     * Implementations must ensure that they return their own type instead of
//     * <code>Durational</code>.
//     *
//     * @param scalar  the scalar to multiply by, not null
//     * @return the new updated durational instance, never null
//     * @throws ArithmeticException if the calculation result overflows
//     */
//    Durational multipliedBy(int scalar) ;
//
//    /**
//     * Returns a new instance with each element in this duration multiplied
//     * by the specified scalar.
//     * <p>
//     * Implementations must ensure that they return their own type instead of
//     * <code>Durational</code>.
//     *
//     * @param scalar  the scalar to multiply by, not null
//     * @return the new updated durational instance, never null
//     * @throws ArithmeticException if the calculation result overflows
//     */
//    Durational dividedBy(int scalar) ;

    //-----------------------------------------------------------------------
    /**
     * Is this instance equal to that specified.
     * <p>
     * The comparison is defined as a comparison of the two duration maps.
     *
     * @param other  the other point instance to compare to, null returns false
     * @return true if this point is equal to the specified second
     */
    boolean equals(Object other);

    /**
     * Returns the hash code for this duration.
     * <p>
     * The hash code is defined as the hash code of the duration map.
     *
     * @return the hash code of the duration
     */
    int hashCode();

}
