/*
 * Written by the members of JCP JSR-310 Expert Group and
 * released to the public domain, as explained at
 * http://creativecommons.org/licenses/publicdomain
 */
package javax.time;

/**
 * A time field representing a year.
 * <p>
 * Year is an immutable time field that can only store a year.
 * It is a type-safe way of representing a year in an application.
 * <p>
 * Static factory methods allow you to constuct instances.
 * The year may be queried using getYear().
 * <p>
 * Year is thread-safe and immutable. 
 * 
 * @author Stephen Colebourne
 */
public final class Year implements Comparable<Year> {

    /**
     * The year being represented.
     */
    private final int year;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>Year</code>.
     *
     * @param year  the year to represent
     */
    public static Year year(int year) {
        return new Year(year);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance with the specified year.
     */
    private Year(int year) {
        this.year = year;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the year value.
     *
     * @return the year
     */
    public int getYear() {
        return year;
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this year instance to another.
     * 
     * @param otherYear  the other year instance, not null
     * @return the comparator value, negative if less, postive if greater
     * @throws NullPointerException if otherYear is null
     */
    public int compareTo(Year otherYear) {
        int thisValue = this.year;
        int otherValue = otherYear.year;
        return (thisValue < otherValue ? -1 : (thisValue == otherValue ? 0 : 1));
    }

    /**
     * Is this year instance greater than the specified year.
     * 
     * @param otherYear  the other year instance, not null
     * @return true if this year is greater
     * @throws NullPointerException if otherYear is null
     */
    public boolean isGreaterThan(Year otherYear) {
        return compareTo(otherYear) > 0;
    }

    /**
     * Is this year instance less than the specified year.
     * 
     * @param otherYear  the other year instance, not null
     * @return true if this year is less
     * @throws NullPointerException if otherYear is null
     */
    public boolean isLessThan(Year otherYear) {
        return compareTo(otherYear) < 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Is this instance equal to that specified, evaluating the year.
     * 
     * @param otherYear  the other year instance, null returns false
     * @return true if the year is the same
     */
    public boolean equals(Object otherYear) {
        if (otherYear instanceof Year) {
            return year == ((Year) otherYear).year;
        }
        return false;
    }

    /**
     * A hashcode for the year object.
     * 
     * @return a suitable hashcode
     */
    public int hashCode() {
        return year;
    }

}
