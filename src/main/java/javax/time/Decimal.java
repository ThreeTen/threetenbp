/*
 * Copyright (c) 2007-2011, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.time.calendar.format.CalendricalParseException;

/**
 * An simple decimal number.
 * <p>
 * This class is immutable and thread-safe.
 */
public final class Decimal implements Comparable<Decimal>, Serializable {

    /**
     * Constant for the zero with no decimal places.
     */
    public static final Decimal ZERO = new Decimal(0, 0);
    /**
     * Serialization version.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The main amount of the decimal.
     */
    private final long value;
    /**
     * The fractional part of the decimal.
     */
    private final long fraction;

    //-----------------------------------------------------------------------
    /**
     * Validates that the input value is not null.
     *
     * @param object  the object to check
     * @param errorMessage  the error to throw
     * @throws NullPointerException if the object is null
     */
    static void checkNotNull(Object object, String errorMessage) {
        if (object == null) {
            throw new NullPointerException(errorMessage);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Creates a fraction with the specified number of decimal places.
     *
     * @return the decimal value, not null
     */
    public static Decimal zero(int decimalPlaces) {
        return new Decimal (0, makeFraction(0, decimalPlaces));
    }

    /**
     * Packs the fraction into 64 bits.
     *
     * @param fractionValue  the fraction value, zero or greater
     * @param decimalPlaces  the number of decimal places, from 0 to 18
     * @return the packed fraction
     */
    public static long makeFraction(long fractionValue, int decimalPlaces) {
        if (fractionValue < 0) {
        	throw new IllegalArgumentException("Invalid fraction value, less than zero: " + fractionValue);
        }
        if (decimalPlaces < 0 || decimalPlaces > 18) {
        	throw new IllegalArgumentException("Invalid decimal places, must be from 0 to 18: " + decimalPlaces);
        }
        long packed = fractionValue << 5 + decimalPlaces;
        return packed;
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains a {@code Decimal} from a whole number value with no decimal places.
     *
     * @param value  the whole number value
     * @return the decimal value, not null
     */
    public static Decimal of(long value) {
        return create(value, 0);
    }

    /**
     * Obtains a {@code Decimal} from a whole number value with a fraction.
     * <p>
     * The fraction is specified as an adjustment to the main value.
     *
     * @param value  the whole number value
     * @param fractionAdjustment  the fractional adjustment to the whole number value, positive or negative
     * @param decimalPlaces  the number of decimal places, from 0 to 18
     * @return the decimal value, not null
     * @throws ArithmeticException if the calculation exceeds the supported range
     */
    public static Decimal of(long value, long fractionAdjustment, int decimalPlaces) {
        return create(value, makeFraction(fractionAdjustment, decimalPlaces));
    }

//    /**
//     * Obtains a {@code Decimal} from a {@code BigDecimal}.
//     *
//     * @param value  the decimal value
//     * @param decimalPlaces  the number of decimal places, from 0 to 18
//     * @return the decimal value, not null
//     * @throws ArithmeticException if the calculation exceeds the supported range
//     */
//    public static Decimal of(BigDecimal value) {
//        checkNotNull(value, "Value must not be null");
//        int decimalPlaces = value.scale();
//        if (decimalPlaces < 0) {
//            value = value.movePointRight(-decimalPlaces);
//            decimalPlaces = 0;
//        }
//        value.
//        return of(value.unscaledValue(), decimalPlaces);
//    }
//
//    /**
//     * Obtains a {@code Decimal} from a {@code BigDecimal} and number of decimal places.
//     *
//     * @param value  the decimal value
//     * @return the decimal value, not null
//     * @throws ArithmeticException if the calculation exceeds the supported range
//     */
//    public static Decimal of(BigInteger value, int decimalPlaces) {
//        checkNotNull(value, "Value must not be null");
//        BigInteger[] divRem = value.divideAndRemainder(BILLION);
//        if (divRem[0].bitLength() > 63) {
//            throw new ArithmeticException("Exceeds capacity of Duration: " + value);
//        }
//        return ofEpochSecond(divRem[0].longValue(), divRem[1].intValue());
//    }

    //-----------------------------------------------------------------------
    /**
     * Parses a string into a {@code Decimal}.
     *
     * @param text  the text to parse, not null
     * @return the decimal value, not null
     * @throws CalendricalParseException if the text cannot be parsed to an {@code Instant}
     */
    public static Decimal parse(final CharSequence text) {
        Decimal.checkNotNull(text, "Text to parse must not be null");
        // TODO: Implement
        throw new UnsupportedOperationException();
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains a {@code Decimal}, checking for constants.
     *
     * @param value  the value
     * @param packedFraction  the packed fraction
     */
    private static Decimal create(long value, long packedFraction) {
        if ((value | packedFraction) == 0) {
            return ZERO;
        }
        return new Decimal(value, packedFraction);
    }

    /**
     * Creates an instance.
     *
     * @param value  the value
     * @param packedFraction  the packed fraction
     */
    private Decimal(long value, long packedFraction) {
        super();
        this.value = value;
        this.fraction = packedFraction;
    }

//    /**
//     * Resolves singletons.
//     *
//     * @return the resolved instance, not null
//     */
//    private Object readResolve() {
//        return (value | fraction) == 0 ? EPOCH : this;
//    }

    //-----------------------------------------------------------------------
    /**
     * Gets the value.
     *
     * @return the value
     */
    public long getValue() {
        return value;
    }

    /**
     * Gets the fractional value.
     *
     * @return the fractional value
     */
    public long getFractionalValue() {
        return (fraction >> 5);
    }

    /**
     * Gets the number of decimal places.
     *
     * @return the number of decimal places, from 0 to 18
     */
    public int getDecimalPlaces() {
        return (int) (fraction & 31);
    }

    /**
     * Gets the packed fraction.
     *
     * @return the packed fraction
     */
    public long getPackedFraction() {
        return fraction;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this decimal with the specified duration added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param amountToAdd  the amount to add, positive or negative, not null
     * @return a {@code Decimal} based on this decimal with the specified amount added, not null
     * @throws ArithmeticException if the calculation exceeds the supported range
     */
    public Decimal plus(Decimal amountToAdd) {
        long valueToAdd = amountToAdd.getValue();
        long fractionToAdd = amountToAdd.getFractionalValue();
        int maxScale = Math.max(getDecimalPlaces(), amountToAdd.getDecimalPlaces());
        
        if ((valueToAdd | fractionToAdd) == 0) {
            return this;
        }
        return plus(valueToAdd, nanosToAdd);
    }

    /**
     * Returns a copy of this decimal with the specified amount added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param amountToAdd  the whole number amount to add, positive or negative
     * @return a {@code Decimal} based on this decimal with the specified amount added, not null
     * @throws ArithmeticException if the calculation exceeds the supported range
     */
    public Decimal plus(long amountToAdd) {
        if (amountToAdd == 0) {
            return this;
        }
        long newValue = MathUtils.safeAdd(value, amountToAdd);
        return create(newValue, fraction);
    }

    /**
     * Returns a copy of this decimal with the specified amount added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param amountToAdd  the amount to add, positive or negative
     * @param fractionToAdd  the fraction to add, positive or negative
     * @return a {@code Decimal} based on this decimal with the specified amount added, not null
     * @throws ArithmeticException if the calculation exceeds the supported range
     */
    private Decimal plus(long amountToAdd, long fractionToAdd) {
        if ((amountToAdd | fractionToAdd) == 0) {
            return this;
        }
        long epochSec = MathUtils.safeAdd(value, amountToAdd);
        epochSec = MathUtils.safeAdd(epochSec, fractionToAdd / NANOS_PER_SECOND);
        fractionToAdd = fractionToAdd % NANOS_PER_SECOND;
        long nanoAdjustment = fraction + fractionToAdd;  // safe int+NANOS_PER_SECOND
        return of(epochSec, nanoAdjustment);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this decimal with the specified amount subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param amountToSubtract  the amount to subtract, positive or negative, not null
     * @return a {@code Decimal} based on this decimal with the specified amount subtracted, not null
     * @throws ArithmeticException if the calculation exceeds the supported range
     */
    public Decimal minus(Decimal amountToSubtract) {
        long secsToSubtract = amountToSubtract.getSeconds();
        int nanosToSubtract = amountToSubtract.getNanoOfSecond();
        if ((secsToSubtract | nanosToSubtract) == 0) {
            return this;
        }
        long secs = MathUtils.safeSubtract(value, secsToSubtract);
        long nanoAdjustment = ((long) fraction) - nanosToSubtract;  // safe int+int
        return ofEpochSecond(secs, nanoAdjustment);
    }

    /**
     * Returns a copy of this decimal with the specified amount subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param amountToSubtract  the amount to subtract, positive or negative
     * @return a {@code Decimal} based on this decimal with the specified amount subtracted, not null
     * @throws ArithmeticException if the calculation exceeds the supported range
     */
    public Decimal minusSeconds(long amountToSubtract) {
        if (amountToSubtract == Long.MIN_VALUE) {
            return plus(Long.MAX_VALUE).plus(1);
        }
        return plus(-amountToSubtract);
    }

    //-----------------------------------------------------------------------
    /**
     * Converts this decimal to a {@code BigDecimal}.
     *
     * @return the equivalent value, not null
     */
    public BigDecimal toBigDecimal() {
        return BigDecimal.valueOf(value).add(BigDecimal.valueOf(getFractionalValue(), getDecimalPlaces()));
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this decimal to the specified decimal.
     * <p>
     * The comparison is based on the amount of the decimals.
     *
     * @param otherDecimal  the other decimal to compare to, not null
     * @return the comparator value, negative if less, positive if greater
     */
    public int compareTo(Decimal otherDecimal) {
        int cmp = MathUtils.safeCompare(value, otherDecimal.value);
        if (cmp != 0) {
            return cmp;
        }
        return MathUtils.safeCompare(fraction, otherDecimal.fraction);
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if this decimal is equal to the specified decimal.
     * <p>
     * The comparison is based on the amount of the decimal including the decimal places.
     *
     * @param otherDecimal  the other decimal, null returns false
     * @return true if the other decimal is equal to this one
     */
    @Override
    public boolean equals(Object otherDecimal) {
        if (this == otherDecimal) {
            return true;
        }
        if (otherDecimal instanceof Decimal) {
            Decimal other = (Decimal) otherDecimal;
            return this.value == other.value &&
                   this.fraction == other.fraction;
        }
        return false;
    }

    /**
     * Returns a hash code for this decimal.
     *
     * @return a suitable hash code
     */
    @Override
    public int hashCode() {
        return ((int) (value ^ (value >>> 32))) + 51 * ((int) (fraction ^ (fraction >>> 32)));
    }

    //-----------------------------------------------------------------------
    /**
     * A string representation of this decimal.
     *
     * @return a string representation of this decimal, not null
     */
    @Override
    public String toString() {
        return toBigDecimal().toPlainString();
    }

}
