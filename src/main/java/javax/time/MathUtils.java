/*
 * Written by the members of JCP JSR-310 Expert Group and
 * released to the public domain, as explained at
 * http://creativecommons.org/licenses/publicdomain
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

    public static int safeAdd(int a, int b) {
        return a + b;  // TODO
    }

    public static int safeSubtract(int a, int b) {
        return a - b;  // TODO
    }

    public static int safeMultiply(int a, int b) {
        return a * b;  // TODO
    }

}
