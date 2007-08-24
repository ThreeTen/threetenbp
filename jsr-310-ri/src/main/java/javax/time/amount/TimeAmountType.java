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

import javax.time.part.TimePart;

/**
 * The type of a measurable amount of time, such as Days or Minutes.
 * <p>
 * TimeAmountType is an abstract class and must be subclassed with care to ensure
 * other classes in the framework operate correctly. All subclasses must be
 * final, immutable, thread-safe and be singletons.
 *
 * @param <P> the time part
 * @author Stephen Colebourne
 */
public abstract class TimeAmountType<P extends TimePart>
        implements Comparable<TimeAmountType<?>> {

    /**
     * Constructor.
     */
    protected TimeAmountType() {
        super();
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the time part that this type provides measurement for.
     *
     * @return the time part, never null
     */
    public abstract TimePart getPart();

    /**
     * Gets the name of the time amount type.
     * <p>
     * Subclasses should use the plural of the part name whenever possible.
     *
     * @return the name of the amount type, never null
     */
    public abstract String getName();

    /**
     * Creates a new instance of the associated time amount using the
     * specified value.
     *
     * @param amount  the amount of time to represent, may be negative
     * @return the time amount, never null
     */
    public abstract TimeAmount<P> createInstance(int amount);

    //-----------------------------------------------------------------------
    /**
     * Converts an amount of time to another time part.
     *
     * @param <T> the type to convert to
     * @param valueToConvert  the value to convert, may be negative
     * @param typeToConvertTo  the type to convert to, not null
     * @return the amount in the requested type
     */
    public <T extends TimePart> TimeAmount<T> convert(int valueToConvert, TimeAmountType<T> typeToConvertTo) {
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this TimeAmountType to another based on time part order.
     * This will return the types from shortest to longest.
     *
     * @param other  the other type to compare to, not null
     * @return the comparator result, negative if less, postive if greater, zero if equal
     * @throws NullPointerException if other is null
     */
    public int compareTo(TimeAmountType<?> other) {
        return this.getPart().compareTo(other.getPart());
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a string representation of the time amount type.
     *
     * @return a description of the amount of time
     */
    @Override
    public String toString() {
        return getName();
    }

}
