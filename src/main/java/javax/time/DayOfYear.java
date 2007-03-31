/*
 * Written by the members of JCP JSR-310 Expert Group and
 * released to the public domain, as explained at
 * http://creativecommons.org/licenses/publicdomain
 */
package javax.time;

/**
 * A time field representing a day of year.
 * <p>
 * DayOfYear is an immutable time field that can only store a day of year.
 * It is a type-safe way of representing a day of year in an application.
 * <p>
 * Static factory methods allow you to constuct instances.
 * The day of year may be queried using getDayOfYear().
 * <p>
 * DayOfYear is thread-safe and immutable. 
 * 
 * @author Stephen Colebourne
 */
public final class DayOfYear implements Comparable<DayOfYear> {

    /**
     * The day of year being represented.
     */
    private final int dayOfYear;

    /**
     * Obtains an instance of <code>DayOfYear</code>.
     *
     * @param dayOfYear  the day of year to represent
     */
    public static DayOfYear dayOfYear(int dayOfYear) {
        return new DayOfYear(dayOfYear);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance with the specified day of year.
     */
    private DayOfYear(int dayOfYear) {
        this.dayOfYear = dayOfYear;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the day of year value.
     *
     * @return the day of year
     */
    public int getDayOfYear() {
        return dayOfYear;
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this day of year instance to another.
     * 
     * @param otherDayOfYear  the other day of year instance, not null
     * @return the comparator value, negative if less, postive if greater
     * @throws NullPointerException if otherDayOfYear is null
     */
    public int compareTo(DayOfYear otherDayOfYear) {
        int thisValue = this.dayOfYear;
        int otherValue = otherDayOfYear.dayOfYear;
        return (thisValue < otherValue ? -1 : (thisValue == otherValue ? 0 : 1));
    }

    /**
     * Is this day of year instance greater than the specified day of year.
     * 
     * @param otherDayOfYear  the other day of year instance, not null
     * @return true if this day of year is greater
     * @throws NullPointerException if otherDayOfYear is null
     */
    public boolean isGreaterThan(DayOfYear otherDayOfYear) {
        return compareTo(otherDayOfYear) > 0;
    }

    /**
     * Is this day of year instance less than the specified day of year.
     * 
     * @param otherDayOfYear  the other day of year instance, not null
     * @return true if this day of year is less
     * @throws NullPointerException if otherDayOfYear is null
     */
    public boolean isLessThan(DayOfYear otherDayOfYear) {
        return compareTo(otherDayOfYear) < 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Is this instance equal to that specified, evaluating the day of year.
     * 
     * @param otherDayOfYear  the other day of year instance, null returns false
     * @return true if the day of year is the same
     */
    public boolean equals(Object otherDayOfYear) {
        if (otherDayOfYear instanceof DayOfYear) {
            return dayOfYear == ((DayOfYear) otherDayOfYear).dayOfYear;
        }
        return false;
    }

    /**
     * A hashcode for the day of year object.
     * 
     * @return a suitable hashcode
     */
    public int hashCode() {
        return dayOfYear;
    }

}
