/*
 * Written by the members of JCP JSR-310 Expert Group and
 * released to the public domain, as explained at
 * http://creativecommons.org/licenses/publicdomain
 */
package javax.time;

/**
 * A moment of a month.
 * <p>
 * Month is an immutable moment that records time information to the precision
 * of a month. For example, the value "September 2007" can be stored in a Month.
 * <p>
 * Static factory methods allow you to constuct instances.
 * <p>
 * Month is thread-safe and immutable. 
 * 
 * @author Stephen Colebourne
 */
public final class Month implements Comparable<Month> {

    /**
     * The year being represented.
     */
    private final Year year;
    /**
     * The month of year being represented.
     */
    private final MonthOfYear monthOfYear;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>Month</code>.
     * 
     * @param year  the year to represent
     * @param monthOfYear  the month of year to represent
     */
    public static Month month(Year year, MonthOfYear monthOfYear) {
        return new Month(year, monthOfYear);
    }

    /**
     * Obtains an instance of <code>Month</code> representing this month.
     * 
     * @return a Month object representing this month
     */
    public static Month thisMonth() {
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance with the specified month of year.
     * 
     * @param year  the year to represent
     * @param monthOfYear  the month of year to represent
     */
    private Month(Year year, MonthOfYear monthOfYear) {
        this.year = year;
        this.monthOfYear = monthOfYear;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the month of year value.
     *
     * @return the month of year
     */
    public Year getYear() {
        return year;
    }

    /**
     * Gets the month of year value.
     *
     * @return the month of year
     */
    public MonthOfYear getMonthOfYear() {
        return monthOfYear;
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this instance to another.
     * 
     * @param otherMonth  the other month instance, not null
     * @return the comparator value, negative if less, postive if greater
     * @throws NullPointerException if otherMonth is null
     */
    public int compareTo(Month otherMonth) {
        int cmp = year.compareTo(otherMonth.year);
        if (cmp != 0) {
            return cmp;
        }
        return monthOfYear.compareTo(otherMonth.monthOfYear);
    }

    /**
     * Is this instance after the specified one.
     * 
     * @param otherMonth  the other month instance, not null
     * @return true if this month of year is greater
     * @throws NullPointerException if otherMonth is null
     */
    public boolean isAfter(Month otherMonth) {
        return compareTo(otherMonth) > 0;
    }

    /**
     * Is this instance before the specified one.
     * 
     * @param otherMonth  the other month instance, not null
     * @return true if this month of year is less
     * @throws NullPointerException if otherMonth is null
     */
    public boolean isBefore(Month otherMonth) {
        return compareTo(otherMonth) < 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Is this instance equal to that specified.
     * 
     * @param otherMonth  the other month instance, null returns false
     * @return true if the month of year is the same
     */
    public boolean equals(Object otherMonth) {
        if (otherMonth instanceof Month) {
            return monthOfYear == ((Month) otherMonth).monthOfYear;
        }
        return false;
    }

    /**
     * A hashcode for the month object.
     * 
     * @return a suitable hashcode
     */
    public int hashCode() {
        return year.hashCode() + 37 * monthOfYear.hashCode();
    }

}
