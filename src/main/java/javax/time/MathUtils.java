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
package javax.time;

/*
 * These methods are proposed for java.lang.Math.
 */
public class MathUtils {

    /**
     * Negates the input value, throwing an exception if an overflow occurs.
     * 
     * @param value  the value to negate
     * @return the negated value
     * @throws ArithmeticException if the value cannot be negated
     */
    public static int safeNegate(int value) {
        if (value == Integer.MIN_VALUE) {
            throw new ArithmeticException("Integer.MIN_VALUE cannot be negated");
        }
        return -value;
    }

    /**
     * Safely adds two int values.
     * 
     * @param a  the first value
     * @param b  the second value
     * @return the result
     * @throws ArithmeticException if the result overflows an int
     */
    public static int safeAdd(int a, int b) {
        int sum = a + b;
        // check for a change of sign in the result when the inputs have the same sign
        if ((a ^ sum) < 0 && (a ^ b) >= 0) {
            throw new ArithmeticException("Addition overflows an int: " + a + " + " + b);
        }
        return sum;
    }

    /**
     * Safely subtracts one int from another.
     * 
     * @param a  the first value
     * @param b  the second value to subtract from the first
     * @return the result
     * @throws ArithmeticException if the result overflows an int
     */
    public static int safeSubtract(int a, int b) {
        int sum = a - b;
        // check for a change of sign in the result when the inputs have the different signs
        if ((a ^ sum) < 0 && (a ^ b) < 0) {
            throw new ArithmeticException("Subtraction overflows an int: " + a + " - " + b);
        }
        return sum;
    }

    /**
     * Safely multiply one int by another.
     * 
     * @param a  the first value
     * @param b  the second value
     * @return the result
     * @throws ArithmeticException if the result overflows an int
     */
    public static int safeMultiply(int a, int b) {
        long total = (long) a * (long) b;
        if (total < Integer.MIN_VALUE || total > Integer.MAX_VALUE) {
            throw new ArithmeticException("Multiplication overflows an int: " + a + " * " + b);
        }
        return (int) total;
    }

}
