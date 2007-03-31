/*
 * Written by the members of JCP JSR-310 Expert Group and
 * released to the public domain, as explained at
 * http://creativecommons.org/licenses/publicdomain
 */
package javax.time;

/**
 * A time field representing a month of year.
 * <p>
 * MonthOfYear is an immutable time field that can only store a month of year.
 * It is a type-safe way of representing a month of year in an application.
 * <p>
 * Static factory methods allow you to constuct instances.
 * The month of year may be queried using getMonthOfYear().
 * <p>
 * MonthOfYear is thread-safe and immutable. 
 * 
 * @author Stephen Colebourne
 */
public final class MonthOfYear implements Comparable<MonthOfYear> {

    /**
     * The month of year being represented.
     */
    private final int monthOfYear;

    /**
     * Obtains an instance of <code>MonthOfYear</code>.
     *
     * @param monthOfYear  the month of year to represent
     */
    public static MonthOfYear monthOfYear(int monthOfYear) {
        return new MonthOfYear(monthOfYear);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance with the specified month of year.
     */
    private MonthOfYear(int monthOfYear) {
        this.monthOfYear = monthOfYear;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the month of year value.
     *
     * @return the month of year
     */
    public int getMonthOfYear() {
        return monthOfYear;
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this month of year instance to another.
     * 
     * @param otherMonthOfYear  the other month of year instance, not null
     * @return the comparator value, negative if less, postive if greater
     * @throws NullPointerException if otherMonthOfYear is null
     */
    public int compareTo(MonthOfYear otherMonthOfYear) {
        int thisValue = this.monthOfYear;
        int otherValue = otherMonthOfYear.monthOfYear;
        return (thisValue < otherValue ? -1 : (thisValue == otherValue ? 0 : 1));
    }

    /**
     * Is this month of year instance greater than the specified month of year.
     * 
     * @param otherMonthOfYear  the other month of year instance, not null
     * @return true if this month of year is greater
     * @throws NullPointerException if otherMonthOfYear is null
     */
    public boolean isGreaterThan(MonthOfYear otherMonthOfYear) {
        return compareTo(otherMonthOfYear) > 0;
    }

    /**
     * Is this month of year instance less than the specified month of year.
     * 
     * @param otherMonthOfYear  the other month of year instance, not null
     * @return true if this month of year is less
     * @throws NullPointerException if otherMonthOfYear is null
     */
    public boolean isLessThan(MonthOfYear otherMonthOfYear) {
        return compareTo(otherMonthOfYear) < 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Is this instance equal to that specified, evaluating the month of year.
     * 
     * @param otherMonthOfYear  the other month of year instance, null returns false
     * @return true if the month of year is the same
     */
    public boolean equals(Object otherMonthOfYear) {
        if (otherMonthOfYear instanceof MonthOfYear) {
            return monthOfYear == ((MonthOfYear) otherMonthOfYear).monthOfYear;
        }
        return false;
    }

    /**
     * A hashcode for the month of year object.
     * 
     * @return a suitable hashcode
     */
    public int hashCode() {
        return monthOfYear;
    }

}
